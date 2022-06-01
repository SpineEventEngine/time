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
import io.spine.internal.catalog.AlwaysReturnDelegate

/**
 * Notation is a language, which is used to declare VersionCatalog-compatible items.
 *
 * This interface describes the base traits of all catalog items.
 *
 * @see CatalogEntry
 */
internal interface CatalogEntryNotation {

    /**
     * A pseudonym, by which an item is known in a version catalog.
     *
     * It is a mandatory property for every item in the catalog.
     */
    val alias: Alias
}

/**
 * Describes how to declare a version item.
 *
 * In order to declare a version, only an [alias] and [version] itself are needed.
 *
 * @see VersionEntry
 */
internal interface VersionNotation : CatalogEntryNotation {

    /**
     * A string value, which denotes a version.
     */
    val version: String
}

/**
 * Describes how to declare a library.
 *
 * In order to declare a library, three components are needed:
 *
 *  1. An [alias] to uniquely identify a library in the catalog.
 *  2. A [module], represented by a group and artifact.
 *  3. A [version].
 *
 *  @see LibraryEntry
 */
internal interface LibraryNotation : VersionNotation {

    /**
     * A group and artifact, separated by a colon.
     *
     * An example: `org.dummy:best-dummy-lib`.
     */
    val module: String
}

/**
 * Describes how to declare a Gradle plugin.
 *
 * In order to declare a plugin, three components are needed:
 *
 *  1. An [alias] to uniquely identify a plugin in the catalog.
 *  2. An [id], by which the plugin is identified in a project and in
 *     Gradle plugins portal.
 *  3. A [version].
 *
 * Optionally, a plugin binary be specified using [module] property.
 *
 *  @see PluginEntry
 */
internal interface PluginNotation : LibraryNotation {

    /**
     * A unique name of the plugin.
     *
     * It is used to identify the plugin in a project and in Gradle plugins portal.
     */
    val id: String
}

/**
 * Describes how to declare a named set of libraries, also known as a bundle.
 */
internal interface BundleNotation : CatalogEntryNotation {

    /**
     * A set of libraries to be referenced as a whole.
     */
    val bundle: Set<LibraryNotation>?
}

/**
 * It is a composite notation, which allows declaring one or more items
 * at once.
 *
 * Within this notation, one can declare: [version], [module] and [bundle].
 *
 * In addition to that, it provides methods to declare libraries and bundles
 * on top of this notation.
 */
internal interface DependencyNotation : LibraryNotation, BundleNotation {

    /**
     * Declares a library on top of this notation.
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
     */
    fun lib(name: String, module: String): LibraryNotation

    /**
     * Declares a library on top of this notation, using property delegation.
     *
     * The name of library will be the same as the property name.
     *
     * An example usage:
     *
     * ```
     * val core by lib("my.company:core-lib")
     * ```
     */
    fun lib(module: String): AlwaysReturnDelegate<LibraryNotation>

    /**
     * Declares a bundle on top of this notation, using property delegation.
     *
     * The name of bundle will be the same as the property name.
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
    fun bundle(vararg libs: LibraryNotation): AlwaysReturnDelegate<BundleNotation>
}
