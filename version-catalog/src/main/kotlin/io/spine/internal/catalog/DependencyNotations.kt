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

package io.spine.internal.catalog

internal interface CatalogEntryNotation {
    val alias: Alias
}

internal interface VersionNotation : CatalogEntryNotation {
    val version: String
}

internal interface LibraryNotation : VersionNotation {
    val module: String
}

internal interface PluginNotation : VersionNotation {

    val id: String

    val module: String?
        get() = null
}

internal interface BundleNotation : CatalogEntryNotation {
    val bundle: Set<LibraryNotation>
}

internal interface DependencyNotation : VersionNotation {

    val module: String?
        get() = null

    val bundle: Set<LibraryNotation>?
        get() = null

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
     *
     * When a library and entry have the same name, it is not duplicated in
     * the alias.
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
     *
     * When a property and entry have the same name, it is not duplicated in
     * the alias.
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
