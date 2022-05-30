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
import io.spine.internal.catalog.record.CatalogRecord
import kotlin.reflect.KClass

internal abstract class CatalogEntry : CatalogEntryNotation {

    // They are lazy by design.
    // If not, it leads to InitializationError.

    // Also, those operations are quite heavy.

    private val nestedEntries: Set<CatalogEntry> by lazy {  nestedEntries() }
    internal val outerEntry: CatalogEntry? by lazy { outerEntry() }
    final override val alias: String = alias()

    open fun records(): Set<CatalogRecord> = emptySet()

    fun allRecords(): Set<CatalogRecord> {
        val result = mutableSetOf<CatalogRecord>()

        val fromThisEntry = records()
        result.addAll(fromThisEntry)

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
        val outerEntry = if (enclosingInstance is CatalogEntry) enclosingInstance else null
        return outerEntry
    }

    /**
     * Smart cast doesn't work, since [outerEntry] is lazy.
     */
    private fun alias(): String {
        val className = this::class.camelName()
        val alias = if (outerEntry != null) "${outerEntry!!.alias}-$className" else className
        return alias
    }

    private fun KClass<*>.camelName() = simpleName!!.replaceFirstChar { it.lowercaseChar() }
}
