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

package io.spine.internal.catalog.model

open class CatalogEntry {

    private val outerEntry: CatalogEntry? = outerEntry()

    private val alias: Alias = alias()

    private val standaloneLibs = mutableSetOf<LibraryRecord>()

    private val standaloneBundles = mutableSetOf<BundleRecord>()

    private val versionRecord: VersionRecord by lazy { versionRecord() }

    open val version: String? = null

    open val module: String? = null

    open val id: String? = null

    open val bundle: Set<Any>? = null

    private fun records(): Set<CatalogRecord> {
        val result = mutableSetOf<CatalogRecord>()

        if (version != null) {
            val record = VersionRecord(alias, version!!)
            result.add(record)
        }

        if (module != null) {
            val record = LibraryRecord(alias, module!!, versionRecord)
            result.add(record)
        }

        if (id != null) {
            val pluginAlias = pluginAlias()
            val record = PluginRecord(pluginAlias, id!!, versionRecord)
            result.add(record)
        }

        if (bundle != null) {
            val libs = toLibraryRecords(bundle!!)
            val record = BundleRecord(alias, libs)
            result.add(record)
        }

        standaloneLibs.forEach { result.add(it) }
        standaloneBundles.forEach { result.add(it) }

        return result
    }

    fun allRecords(): Set<CatalogRecord> {
        val result = mutableSetOf<CatalogRecord>()

        val fromThisEntry = records()
        result.addAll(fromThisEntry)

        val nestedEntries = nestedEntries()
        val fromNested = nestedEntries.flatMap { it.allRecords() }
        result.addAll(fromNested)

        return result
    }

    fun lib(name: String, module: String): LibraryRecord {
        val thisEntryAlias = this.alias
        val libAlias = if (thisEntryAlias.endsWith(name)) thisEntryAlias
        else "$thisEntryAlias-$name"

        val record = LibraryRecord(libAlias, module, versionRecord)
        standaloneLibs.add(record)

        return record
    }

    fun lib(module: String): MemoizingDelegate<LibraryRecord> =
        delegate { property -> lib(property.name, module) }

    fun bundle(vararg libs: Any): MemoizingDelegate<BundleRecord> =
        delegate { property ->
            val thisEntryAlias = this.alias
            val bundleAlias = "$thisEntryAlias-${property.name}"

            val libRecords = toLibraryRecords(libs.asIterable())
            val record = BundleRecord(bundleAlias, libRecords)
            standaloneBundles.add(record)

            record
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

    private fun toLibraryRecords(obj: Iterable<Any>): Set<LibraryRecord> =
        obj.map { toLibraryRecord(it) }.toSet()

    private fun toLibraryRecord(obj: Any): LibraryRecord = when (obj) {
        is LibraryRecord -> obj
        is CatalogEntry -> LibraryRecord(obj.alias, obj.module!!, obj.versionRecord)
        else -> throw IllegalArgumentException("Unknown object has been passed: $obj!")
    }

    private fun versionRecord(): VersionRecord = when {
        version != null -> VersionRecord(alias, version!!)
        outerEntry != null -> outerEntry.versionRecord
        else -> throw IllegalStateException("Specify version in this entry or any parent one!")
    }

    private fun pluginAlias(): Alias = alias.removeSuffix("-gradlePlugin")
}
