/*
 * Copyright 2022, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.internal.catalog.entry

import io.spine.internal.catalog.CatalogEntryNotation
import io.spine.internal.catalog.CatalogRecord

/**
 * A skeleton implementation of a catalog entry.
 *
 * The main idea behind the concept of entries is to provide a declarative way
 * to create version catalog items. Entries expose a declarative API, leaving
 * behind the scene all imperative code.
 *
 * Only object declarations are meant to serve as concrete entries. And thanks
 * to Kotlin, they can be easily nested one in another.
 *
 * For example:
 *
 * ```
 * internal object MyCatalogEntry : SomeEntryType() {
 *     object SubEntry1 : SomeEntryType()
 *     object SubEntry2 : SomeEntryType()
 * }
 * ```
 *
 * As a skeleton, this class takes a responsibility for the following:
 *
 *  1. Support of nesting. An entry can nest one or more other entries.
 *  2. Automatic aliasing for entries. An alias is picked up from the object's
 *     name, taking into account its nesting.
 *
 * As the ultimate goal of its existence, the class provides [allRecords] method.
 * The method produces a set of [CatalogRecord]s, using all declarations that
 * are done within this entry and its nested entries. Then, those records can be
 * directly [written][CatalogRecord.writeTo] into a version catalog.
 *
 * It is worth to mention, that the relationship between an entry and records
 * it produces is "one to many". It means that a single entry can produce zero,
 * one or more records.
 */
internal abstract class AbstractCatalogEntry : CatalogEntryNotation {

    /**
     * A parent entry, within which this entry resides, if present.
     */
    protected val outerEntry: CatalogEntryNotation? = outerEntry()

    /**
     * Object's name with respect to entity's nesting.
     *
     * We say "object" here because all concrete entries are meant to be
     * object declarations.
     *
     * Please, consider the following example in order to grasp a principle of aliasing:
     *
     * ```
     * internal object Kotlin : SomeEntry() { // alias = `kotlin`
     *     object Coroutines : SomeEntry()    // alias = `kotlin-coroutines`
     *     object Runtime : SomeEntry() {     // alias = `kotlin-runtime`
     *         object Linux : SomeEntry()     // alias = `kotlin-runtime-linux`
     *         object Mac : SomeEntry()       // alias = `kotlin-runtime-mac`
     *     }
     * }
     * ```
     */
    final override val alias: String = alias()

    /**
     * Obtains all catalog records, produced by this entry.
     */
    abstract fun records(): Set<CatalogRecord>

    /**
     * Obtains all catalog records, produced by this entry and its nested entries.
     */
    fun allRecords(): Set<CatalogRecord> {
        val result = mutableSetOf<CatalogRecord>()

        val fromThisEntry = records()
        result.addAll(fromThisEntry)

        val nestedAliases = nestedEntries()
        val fromNested = nestedAliases.flatMap { it.allRecords() }
        result.addAll(fromNested)

        return result
    }

    private fun nestedEntries(): Set<AbstractCatalogEntry> {
        val nestedClasses = this::class.nestedClasses
        val nestedObjects = nestedClasses.mapNotNull { it.objectInstance }
        val nestedEntries = nestedObjects.filterIsInstance<AbstractCatalogEntry>()
        return nestedEntries.toSet()
    }

    private fun outerEntry(): CatalogEntryNotation? {
        val enclosingClass = this::class.java.enclosingClass
        val enclosingInstance = enclosingClass?.kotlin?.objectInstance
        val outerEntry = if (enclosingInstance is CatalogEntryNotation) enclosingInstance else null
        return outerEntry
    }

    private fun alias(): String {
        val className = this::class.java.simpleName.toCamelCase()
        val alias = if (outerEntry != null) "${outerEntry.alias}-$className" else className
        return alias
    }

    private fun String.toCamelCase() = replaceFirstChar { it.lowercaseChar() }
}
