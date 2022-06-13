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

/**
 * Declaration of a dependency, which is going to become a part of a version catalog.
 *
 * The main idea behind the concept of entries is to provide a declarative way
 * to define catalog items: versions, libraries, plugins and bundles. Entries expose
 * a declarative API, leaving behind the scene all imperative code.
 *
 * Out of declarations, which are done within an entry, it [produces][allRecords]
 * a set of [CatalogRecord]s. Once produced, the records can be directly [written][CatalogRecord.writeTo]
 * into a version catalog. The relationship between an entry and records it produces
 * is "one to many". It means that a single entry can produce zero, one or more records.
 *
 * # Usage
 *
 * Please note, only object declarations are meant to inherit from this class
 * and serve as concrete entries.
 *
 * For example:
 *
 * ```
 * internal object MyLib : CatalogEntry() {
 *     // ...
 * }
 * ```
 *
 * ## Nesting
 *
 * One entry can be put into another one. When an entry is [asked][allRecords]
 * for records, it will propagate the request down to its nested entries.
 * Thus, only root entries should be used to produce records.
 *
 * ## Aliasing
 *
 * Each item in a version catalog should have [Alias]. An entry sets aliases on
 * its own for items, declared within it. Automatic aliasing takes into account
 * entry's nesting.
 *
 * One can declare an entry, which only hosts other entries. Thus, providing
 * a named scope for other declarations.
 *
 * Consider the following example:
 *
 * ```
 * internal object Runtime : CatalogEntry() { // alias = "runtime"
 *     object Linux : CatalogEntry() // alias = "runtime-linux"
 *     object Mac : CatalogEntry()   // alias = "runtime-mac"
 *     object Win : CatalogEntry()   // alias = "runtime-win"
 * }
 * ```
 *
 * See documentation to [Alias] to see how a type-safe accessor is generated
 * from an alias.
 *
 * ## Declaring versions
 *
 * An entry which declares only a bare version is a quite rare case. Such entries
 * can be used to declare a version of used tools.
 *
 * An example of how to declare a bare version:
 *
 * ```
 * internal object MyTool : CatalogEntry() {
 *     override val version = "1.0.0"
 * }
 * ```
 *
 * ## Declaring libraries
 *
 * The most common case is a declaring a library. Most entries just declare a
 * single library.
 *
 * An example of how to declare a library:
 *
 * ```
 * internal object MyLib : CatalogEntry() {
 *     override val version = "1.0.0"
 *     override val module = "com.company:my-lib"
 * }
 * ```
 *
 * Sometimes, a dependency consists of several libraries. Or even of a group
 * of libraries. A group can be extracted into a nested entry, and extra libraries
 * can be declared by a [delegated property][lib].
 *
 * For example:
 *
 * ```
 * internal object MyLib : CatalogEntry() {
 *     private const val group = "com.company"
 *     override val version = "1.9.0"
 *     override val module = "$group:my-lib"
 *
 *     val runner by lib("$group:my-runner")
 *     val linter by lib("$group:my-linter")
 *
 *     object Adapters : CatalogEntry() {
 *         val html4 by lib("$group:html4-adapter")
 *         val html5 by lib("$group:html5-adapter")
 *     }
 * }
 * ```
 *
 * Please note, that nested `Adapters` entry doesn't declare a version. In cases,
 * when a version is not declared, an entry will try to fetch it from the closest
 * parental entry, which has one. If the version is needed, but isn't found,
 * an entry will throw an exception.
 *
 * ## Declaring plugins
 *
 * The minimum, required to declare a plugin is a [version] and [id].
 *
 * For example:
 *
 * ```
 * internal object MyPlugin : CatalogEntry() {
 *     override val version = "1.0.0"
 *     override val id = "com.company.plugin"
 * }
 * ```
 *
 * A standalone plugin is also a quite rare case. Usually, they are declared
 * within more complex entries, which represent frameworks or big libraries.
 * A plugin can also be supplemented with a library, that makes possible applying
 * it from `buildSrc`.
 *
 * For example:
 *
 * ```
 * internal object MyLib : CatalogEntry() {
 *     private const val group = "com.company"
 *     // ...
 *
 *     object GradlePlugin : CatalogEntry() {
 *         override val version = "1.2.3"           // libs.versions.myLib.gradlePlugin
 *         override val module = "$group:my-plugin" // libs.myLib.gradlePlugin
 *         override val id = "$group.plugin"        // libs.plugins.myLib (without `gradlePlugin`!)
 *     }
 * }
 * ```
 *
 * In the example above, comments show the resulted generated accessors. Please note,
 * that `GradlePlugin` is a special name for entries. Such an entry will not append
 * `gradlePlugin` suffix for [id] item. It is done in order not to repeat yourself
 * in naming. Otherwise, we would come up with this: `libs.plugins.myLib.gradlePlugin`.
 *
 * ## Declaring bundles
 *
 * A bundle is a named set of libraries. One can compose a bundle out of already
 * declared extra libraries, in-place library declarations or entries, which
 * declare a library.
 *
 * For example:
 *
 * ```
 * internal object MyLib : CatalogEntry() {
 *     private const val group = "com.company"
 *     override val version = "1.9.0"
 *     override val module = "$group:my-lib"
 *
 *     object Adapters : CatalogEntry() {
 *         val html4 by lib("$group:html4-adapter")
 *         val html5 by lib("$group:html5-adapter")
 *     }
 *
 *     object Runner : CatalogEntry() {
 *         override val version = "18.51.0"
 *         override val module = "$group:runner"
 *     }
 *
 *     override val bundle = setOf(
 *
 *         // Entries, which declare `module`.
 *         this, Runner,
 *
 *         // Extra libraries, declared by `lib` delegate.
 *         Adapters.html4,
 *         Adapters.html5,
 *
 *         // In-place declarations.
 *         lib("linter", "$group:linter"),
 *         lib("core", "$group:core"),
 *     )
 * }
 * ```
 *
 * There's also a possibility to declare extra bundles on top of the current entry.
 * Just like with extra libraries, using a [property delegate][bundle].
 *
 * For example:
 *
 * ```
 * internal object MyLib : CatalogEntry() {
 *     private const val group = "com.company"
 *     override val version = "1.9.0"
 *     override val module = "$group:my-lib"
 *
 *     object Adapters : CatalogEntry() {
 *         val html4 by lib("$group:html4-adapter")
 *         val html5 by lib("$group:html5-adapter")
 *     }
 *
 *     val adapters by bundle(
 *         Adapters.html4,
 *         Adapters.html5,
 *     )
 * }
 * ```
 */
abstract class CatalogEntry {

    private val extraLibs = mutableSetOf<LibraryRecord>()
    private val extraBundles = mutableSetOf<BundleRecord>()
    private val versionRecord: VersionRecord by lazy { versionRecord() }

    /**
     * A direct parent of this entry, if any.
     */
    internal val outerEntry: CatalogEntry? = outerEntry()

    /**
     * An alias of this entry.
     *
     * All items, declared within this entry, will receive this alias.
     *
     * [Extra libraries][lib] and [bundles][bundle], declared on top of this
     * entry, will also use this alias as a base for their own aliases.
     */
    @Suppress("LeakingThis") // `Alias.forEntry()` uses only final properties.
    internal val alias: Alias = Alias.forEntry(this)

    /**
     * Optionally, this entry can declare a version.
     *
     * In order to do that, override this property, specifying a string
     * representation of a version.
     *
     * For example: `2.0.0-SNAPSHOT.21`.
     *
     * A declared version will inherit entry's [alias].
     */
    open val version: String? = null

    /**
     * Optionally, this entry can declare a library.
     *
     * In order to do that, override this property, specifying a group and
     * artifact of a library, seperated by a colon.
     *
     * For example: `io.spine:spine-core`.
     *
     * A declared library will inherit entry's [alias].
     *
     * When declaring [module], make sure that this entry or any parental one
     * declares [version]. A library can't be declared without version.
     */
    open val module: String? = null

    /**
     * Optionally, this entry can declare a plugin.
     *
     * In order to do that, override this property, specifying a plugin id.
     *
     * For example: `org.jetbrains.kotlin.jvm`.
     *
     * A declared plugin will inherit entry's [alias].
     *
     * When declaring [id], make sure that this entry or any parental one
     * declares [version]. A plugin can't be declared without version.
     */
    open val id: String? = null

    /**
     * Optionally, this entry can declare a bundle (named set of libraries).
     *
     * In order to do that, override this property, specifying a set of libraries.
     *
     * The following declarations are acceptable to be passed into this set:
     *
     *  1. [CatalogEntry]s which declare [module].
     *  2. Extra libraries, declared by a [delegated property][lib].
     *  3. Extra libraries, declared by a [method][lib] right in this set.
     *
     *  An example snippet is present in `Declaring bundles` section of
     *  documentation to [CatalogEntry].
     *
     * A declared bundle will inherit entry's [alias].
     */
    open val bundle: Set<Any>? = null

    /**
     * Declares an extra library on top of this entry, using property delegation.
     *
     * An example usage:
     *
     * ```
     * val core by lib("my.company:core-lib")
     * ```
     *
     * The resulting alias fot this library is entry's [alias] followed by
     * a property name.
     */
    fun lib(module: String): MemoizingDelegate<CatalogRecord> =
        delegate { property -> lib(property.name, module) }

    /**
     * Declares an extra library on top of this entry.
     *
     * This method allows declaring libraries right in bundle declarations:
     *
     * ```
     * val bundle = setOf(
     *     lib("core", "my.company:core-lib"),
     *     lib("types", "my.company:types-lib"),
     *     lib("lang", "my.company:lang-lib")
     * )
     * ```
     *
     * The resulting alias fot this library is entry's [alias] followed by
     * the given name.
     */
    fun lib(name: String, module: String): CatalogRecord {
        val libAlias = alias + name
        val record = LibraryRecord(libAlias, module, versionRecord)
        return record.also { extraLibs.add(it) }
    }

    /**
     * Declares an extra bundle on top of this entry, using property delegation.
     *
     * The following declarations are acceptable to be passed into this set:
     *
     *  1. [CatalogEntry]s which declare [module].
     *  2. Extra libraries, declared by a [delegated property][lib].
     *  3. Extra libraries, declared by a [method][lib] right in this set.
     *
     *  An example snippet is present in `Declaring bundles` section of
     *  documentation to [CatalogEntry].
     *
     * The resulting alias fot this library is entry's [alias] followed by
     * a property name.
     */
    fun bundle(vararg libs: Any): MemoizingDelegate<CatalogRecord> =
        delegate { property ->
            val bundleAlias = alias + property.name
            val libRecords = libs.asIterable().toLibraryRecords()
            val record = BundleRecord(bundleAlias, libRecords)
            record.also { extraBundles.add(it) }
        }

    /**
     * Produces [CatalogRecord]s out of declarations, performed within this
     * entry and its nested entries.
     *
     * When an entry is asked for records, it will propagate the request down
     * to its nested entries. For this reason, this method should be called only
     * on top-level (root) entries.
     *
     * Calling of this method on a nested entry leads to an exception.
     */
    fun allRecords(): Set<CatalogRecord> {

        if (outerEntry != null) {
            throw IllegalStateException("Only root entries can produce records!")
        }

        val allRecords = records()
        return allRecords
    }

    private fun records(): Set<CatalogRecord> {
        val fromThisEntry = recordsFromThisEntry()
        val fromNested = recordsFromNested()
        val result = fromThisEntry + fromNested
        return result
    }

    private fun recordsFromThisEntry(): Set<CatalogRecord> {
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
            val pluginAlias = alias - "gradlePlugin"
            val record = PluginRecord(pluginAlias, id!!, versionRecord)
            result.add(record)
        }

        if (bundle != null) {
            val libs = bundle!!.toLibraryRecords()
            val record = BundleRecord(alias, libs)
            result.add(record)
        }

        extraLibs.forEach { result.add(it) }
        extraBundles.forEach { result.add(it) }

        return result
    }

    private fun recordsFromNested(): Set<CatalogRecord> {
        val result = mutableSetOf<CatalogRecord>()
        val nestedEntries = nestedEntries()
        val fromNested = nestedEntries.flatMap { it.records() }
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

    private fun versionRecord(): VersionRecord = when {
        version != null -> VersionRecord(alias, version!!)
        outerEntry != null -> outerEntry.versionRecord
        else -> throw IllegalStateException("Specify version in this entry or any parental one!")
    }

    private fun Iterable<Any>.toLibraryRecords(): Set<LibraryRecord> {
        val result = map { it.toLibraryRecord() }.toSet()
        return result
    }

    private fun Any.toLibraryRecord(): LibraryRecord {
        if (this is LibraryRecord) {
            return this
        }

        if (this is CatalogEntry) {
            val entry = this
            val record = LibraryRecord(entry.alias, entry.module!!, entry.versionRecord)
            return record
        }

        throw IllegalArgumentException("Unknown object has been passed: $this!")
    }
}
