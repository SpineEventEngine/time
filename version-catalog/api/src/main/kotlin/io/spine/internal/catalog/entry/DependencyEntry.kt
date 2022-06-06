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

import io.spine.internal.catalog.AlwaysReturnDelegate
import io.spine.internal.catalog.BundleNotation
import io.spine.internal.catalog.BundleRecord
import io.spine.internal.catalog.CatalogRecord
import io.spine.internal.catalog.LibraryNotation
import io.spine.internal.catalog.LibraryRecord
import io.spine.internal.catalog.delegate

/**
 * A compound catalog entry, which is used to declare complex dependencies.
 *
 * Although, many dependencies can be represented by a single [LibraryEntry],
 * some are a way more complex. They may consist of several modules, versions
 * and bundles. For example, frameworks.
 *
 * This entry can declare a library and version. But, in case it is all you need,
 * it is better to use a simpler entries: [VersionEntry] and [LibraryEntry].
 *
 * An example of a version and module declaration:
 *
 * ```
 * internal object MyLib : DependencyEntry() {
 *     override val version = "1.9.0"            // libs.versions.myLib
 *     override val module = "ua.company:my-lib" // libs.myLib
 * }
 * ```
 *
 * The main feature of this entry is a possibility to declare extra [libraries][lib]
 * and [bundles][bundle] on top of this entry, using property delegation.
 *
 * An example of declaring several extra libraries and an extra bundle:
 *
 * ```
 * internal object MyLib : DependencyEntry() {
 *
 *     override val version = "1.9.0"            // libs.versions.myLib
 *     override val module = "ua.company:my-lib" // libs.myLib
 *
 *     object Adapters : DependencyEntry() {
 *         val html4 by lib("ua.company:html4-adapter") // libs.myLib.adapters.html4
 *         val html5 by lib("ua.company:html5-adapter") // libs.myLib.adapters.html5
 *     }
 *
 *     val adapters by bundle( // libs.bundles.myLib.adapters
 *         Adapters.html4,
 *         Adapters.html5
 *     )
 * }
 * ```
 *
 * Even more, a code snippet above can be re-written even simpler, using in-place
 * bundle declaration and method calls to declare extra libraries.
 *
 * Below is the same example as above, but re-written with in-place API:
 *
 * ```
 * internal object MyLib : DependencyEntry() {
 *
 *     override val version = "1.9.0"            // libs.versions.myLib
 *     override val module = "ua.company:my-lib" // libs.myLib
 *
 *     object Adapters : DependencyEntry() {
 *         override val bundle = setOf(
 *             lib("html4", "ua.company:html4-adapter"),
 *             lib("html5", "ua.company:html5-adapter")
 *         )
 *     }
 * }
 * ```
 *
 * Bundles can't include entries, within which they are declared. A [DependencyEntry]
 * can't guarantee that a library is declared within it. Library declaration is
 * optional for such entries.
 *
 * Something like this is not possible:
 *
 * ```
 * internal object MyLib : DependencyEntry() {
 *
 *     override val version = "1.9.0"            // libs.versions.myLib
 *     override val module = "ua.company:my-lib" // libs.myLib
 *
 *     val types by lib("ua.company:my-types") // libs.myLib.types
 *     val data by lib("ua.company:my-data")   // libs.myLib.data
 *
 *     // Referencing to [this] is not supported.
 *     override val bundle = setOf(this, types, data) // libs.bundles.myLib
 * }
 * ```
 *
 * When one needs a bundle and library with the same name, (or even more, that
 * bundle should include that library) one can declare an extra library, named
 * after the entry in which it is declared. It is a special case for extra libraries.
 * In this case, the entry will not concat its name with library's one.
 *
 * Let's re-write a snippet above with that knowledge:
 *
 * ```
 * internal object MyLib : DependencyEntry() {
 *
 *     override val version = "1.9.0"            // libs.versions.myLib
 *
 *     val myLib by lib("ua.company:my-lib")   // libs.myLib (not libs.myLib.myLib!)
 *     val types by lib("ua.company:my-types") // libs.myLib.types
 *     val data by lib("ua.company:my-data")   // libs.myLib.data
 *
 *     override val bundle = setOf(myLib, types, data) // libs.bundles.myLib
 * }
 * ```
 */
open class DependencyEntry : AbstractVersionInheritingEntry() {

    private val standaloneLibs = mutableSetOf<LibraryNotation>()
    private val standaloneBundles = mutableSetOf<BundleNotation>()

    /**
     * Optionally, this entry can declare a module.
     *
     * When declaring a module, make sure the entry has a version to compose a [LibraryRecord].
     * The version can be declared right in this entry or inherited from the outer one.
     */
    open val module: String? = null

    /**
     * Optionally, this entry can declare a bundle.
     *
     * The bundle elements can be: [LibraryEntry]s and extra libraries.
     *
     * An example usage is provided in documentation to this class.
     */
    open val bundle: Set<LibraryNotation>? = null

    /**
     * This entry is very flexible in how many records it can produce.
     *
     * It may produce zero, one or more: [LibraryRecord]s and [BundleRecord]s.
     *
     * And also, it may produce zero or one [io.spine.internal.catalog.VersionRecord].
     */
    override fun records(): Set<CatalogRecord> {
        val result = mutableSetOf<CatalogRecord>()

        val optionalVersion = super.records()
        result.addAll(optionalVersion)

        if (module != null) {
            val library = LibraryRecord(alias, module!!, versionAlias)
            result.add(library)
        }

        if (bundle != null) {
            val aliases = bundle!!.map { it.alias }.toSet()
            val bundle = BundleRecord(alias, aliases)
            result.add(bundle)
        }

        val extraBundles = standaloneBundles.map { record(it) }
        result.addAll(extraBundles)

        val extraLibraries = standaloneLibs.map { record(it) }
        result.addAll(extraLibraries)

        return result
    }

    private fun record(notation: BundleNotation): BundleRecord {
        val aliases = notation.bundle.map { it.alias }.toSet()
        val record = BundleRecord(notation.alias, aliases)
        return record
    }

    private fun record(notation: LibraryNotation): LibraryRecord {
        val record = LibraryRecord(notation.alias, notation.module, versionAlias)
        return record
    }

    /**
     * Declares a library on the top of this entry.
     *
     * This method is useful to declare libraries right in a bundle declaration:
     *
     * ```
     * val bundle = setOf(
     *     lib("core", "my.company:core-lib"),
     *     lib("types", "my.company:types-lib"),
     *     lib("lang", "my.company:lang-lib")
     * )
     * ```
     *
     * When a property and entry have the same name, it is not duplicated in
     * the resulting alias of the library.
     */
    fun lib(name: String, module: String): LibraryNotation {
        val thisEntryAlias = this.alias
        val libAlias = if (thisEntryAlias.endsWith(name)) thisEntryAlias
                       else "$thisEntryAlias-$name"

        val notation = object : LibraryNotation {
            override val alias: String = libAlias
            override val version: String = "" // will be taken from this entry
            override val module: String = module
        }

        standaloneLibs.add(notation)
        return notation
    }

    /**
     * Declares a library on the top of this entry, using property delegation.
     *
     * The name of library will be the same as a property name.
     *
     * An example usage:
     *
     * ```
     * val core by lib("my.company:core-lib")
     * ```
     *
     * When a property and entry have the same name, it is not duplicated in
     * the resulting alias of the library.
     */
    fun lib(module: String): AlwaysReturnDelegate<LibraryNotation> =
        delegate { property -> lib(property.name, module) }

    /**
     * Declares a bundle on top of this entry, using property delegation.
     *
     * The name of a bundle will be the same as a property name.
     *
     * An example usage:
     *
     * ```
     * val runtime by bundle(
     *     lib("mac", "my.company:mac-lib"),
     *     lib("linux", "my.company:linux-lib"),
     *     lib("win", "my.company:win-lib")
     * )
     * ```
     */
    fun bundle(vararg libs: LibraryNotation): AlwaysReturnDelegate<BundleNotation> =
        delegate { property ->
            val thisEntryAlias = this.alias
            val bundleAlias = "$thisEntryAlias-${property.name}"

            val notation = object : BundleNotation {
                override val alias: String = bundleAlias
                override val bundle: Set<LibraryNotation> = libs.toSet()
            }

            standaloneBundles.add(notation)
            notation
        }
}
