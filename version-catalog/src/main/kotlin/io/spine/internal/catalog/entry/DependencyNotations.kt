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
 * Notation is a domain-specific language, which is used to declare
 * VersionCatalog-compatible entries.
 *
 * @see [CatalogEntry]
 */
internal interface CatalogEntryNotation {

    /**
     * A pseudonym, by which an item is known in a version catalog.
     */
    val alias: Alias
}

/**
 * Describes how to declare a version.
 *
 * In order to declare a version, only an alias and version itself are needed.
 *
 * @see [VersionEntry]
 */
internal interface VersionNotation : CatalogEntryNotation {
    val version: String?
}

/**
 * Describes how to declare a library.
 *
 * In order to declare a library, three components are needed:
 *
 *  1. An aliases to uniquely identify a library in the catalog.
 *  2. A module, represented by a group and artifact.
 *  3. A version.
 *
 *  @see [LibraryEntry]
 */
internal interface LibraryNotation : VersionNotation {

    /**
     * A group and artifact, separated by a colon.
     *
     * An example: `org.dummy:best-dummy-lib`.
     */
    val module: String?
}

/**
 * Describes how to declare a Gradle plugin.
 *
 * In order to declare a plugin, four components are needed:
 *
 *  1. An aliases to uniquely identify a plugin in the catalog.
 *  2. A plugin's binary, represented by a module.
 *  3. An identifier, by which the plugin is denoted in the project.
 *  4. A version.
 *
 *  @see PluginEntry
 */
internal interface PluginNotation : LibraryNotation {

    /**
     * A unique name of the plugin.
     *
     * It is used to identify the plugin in a project and in Gradle plugins portal.
     */
    val id: String?
}

/**
 * Describes how to declare a named set of libraries, also known as a bundle.
 *
 * There's no way to declare a standalone bundle. Bundles are declared only
 * within [DependencyNotation].
 */
internal interface BundleNotation : CatalogEntryNotation {

    /**
     * A set of libraries to be referenced as a whole.
     */
    val bundle: Set<LibraryNotation>?
}

// => DependencyEntry
internal interface DependencyNotation : LibraryNotation, BundleNotation {

    fun lib(name: String, module: String): LibraryNotation

    fun lib(module: String): AlwaysReturnDelegate<LibraryNotation>

    fun bundle(vararg libs: LibraryNotation): AlwaysReturnDelegate<BundleNotation>
}
