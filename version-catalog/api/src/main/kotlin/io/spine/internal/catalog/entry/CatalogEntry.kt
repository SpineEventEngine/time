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

import io.spine.internal.catalog.Alias
import io.spine.internal.catalog.CatalogNotation
import io.spine.internal.catalog.CatalogRecord

/**
 * A base catalog entry.
 *
 * The main idea behind the concept of entries is to provide a declarative way to
 * define catalog items. Entries expose a declarative API, leaving behind the scene
 * all imperative code. Entries resemble a bridge between notations (a language,
 * used to declare items) and records (the way, items are stored in the catalog).
 *
 * Only object declarations are meant to serve as concrete entries.
 *
 * As a base entry, this class takes a responsibility for the following:
 *
 *  1. Support of nesting. An entry can nest one or more other entries.
 *  2. Automatic aliasing for entries. An alias is picked up from the object's name,
 *     taking into account its nesting.
 *
 * The primary goal achieved by the `CatalogEntry` objects is to [produce][allRecords]
 * a set of [CatalogRecord]s. It combines all the declarations within this entry and
 * its nested entries. Once produced, the records can be directly [written][CatalogRecord.writeTo]
 * into a version catalog.
 *
 * It is worth to mention, that the relationship between an entry and records it
 * produces is "one to many". It means that a single entry can produce zero, one
 * or more records.
 *
 * A base entry doesn't produce any records. It can only be used as an outer entry
 * for introducing a common alias. Such entries don't declare anything, they just
 * serve as a named scope for nested declarations.
 *
 * Below is an example of `Runtime` entry:
 *
 * ```
 * internal object Runtime : CatalogEntry() { // alias = runtime
 *     object Linux : SomeEntry() // alias = runtime.linux
 *     object Mac : SomeEntry()   // alias = runtime.mac
 *     object Win : SomeEntry()   // alias = runtime.win
 * }
 * ```
 *
 * In the example above, `Linux`, `Mac` and `Win` are concrete entries, which may
 * produce concrete records (such as a library, version, etc.). Meanwhile, `Runtime`
 * does not produce anything. It's just hosting other entries, affecting their final alias.
 *
 * See also: [CatalogNotation], [CatalogRecord].
 */
open class CatalogEntry : CatalogNotation {

    /**
     * A parent entry, within which this entry resides, if present.
     */
    protected val outerEntry: CatalogEntry? = outerEntry()

    /**
     * Object's name with respect to entity's nesting.
     *
     * We say "object" here because all concrete entries are meant to be
     * object declarations.
     */
    final override val alias: Alias = alias()

    /**
     * A base entry produce no records.
     */
    protected open fun records(): Set<CatalogRecord> = emptySet()

    /**
     * Obtains all catalog records, produced by this entry and its nested entries.
     */
    fun allRecords(): Set<CatalogRecord> {
        val result = mutableSetOf<CatalogRecord>()

        val fromThisEntry = records()
        result.addAll(fromThisEntry)

        val nestedEntries = nestedEntries()
        val fromNested = nestedEntries.flatMap { it.allRecords() }
        result.addAll(fromNested)

        return result
    }

    private fun nestedEntries(): Set<CatalogEntry> {
        val nestedClasses = this::class.nestedClasses
        val nestedObjects = nestedClasses.mapNotNull { it.objectInstance }
        val nestedEntries = nestedObjects.filterIsInstance<CatalogEntry>()
        return nestedEntries.toSet()
    }

    private fun outerEntry(): CatalogEntry? {
        val enclosingClass = this::class.java.enclosingClass
        val enclosingInstance = enclosingClass?.kotlin?.objectInstance

        if (enclosingInstance !is CatalogEntry?) {
            throw IllegalStateException("Plain objects can't nest entries!")
        }

        return enclosingInstance
    }

    private fun alias(): String {
        val className = this::class.java.simpleName.toCamelCase()
        val alias = if (outerEntry != null) "${outerEntry.alias}-$className" else className
        return alias
    }

    private fun String.toCamelCase() = replaceFirstChar { it.lowercaseChar() }
}
