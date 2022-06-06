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

/**
 * Defines what information is necessary to create one or another
 * version catalog-compatible item.
 *
 * Notations are citizens of a declaration site. They from DSL and say what is
 * needed from the point of view of who provides this information. In other words,
 * they define what input is required from a user.
 *
 * They are used for the following purposes:
 *
 *  1. To set a language, which is then implemented by entries (link below).
 *  2. To hold data. Sometimes, one item needs to reference another item.
 *     This communication can be done via notations. For example, property
 *     delegates in `DependencyEntry` return notations, which can be consumed
 *     by other delegates or methods.
 *
 * This interface, in particular, doesn't describe any concrete item in the catalog.
 * It just serves as a common foundation for other notations.
 *
 * Direct implementation: [CatalogEntry][io.spine.internal.catalog.entry.CatalogEntry].
 */
interface CatalogNotation {

    /**
     * A name, by which this item will be known in the catalog.
     *
     * For example: `kotlin.stdLib.common.jvm`.
     */
    val alias: Alias
}

/**
 * Information, required to create a version item.
 *
 * Direct implementation: [VersionEntry][io.spine.internal.catalog.entry.VersionEntry].
 */
interface VersionNotation : CatalogNotation {

    /**
     * A string value, which denotes a version.
     *
     * For example: `2.0.0-SNAPSHOT.21`.
     */
    val version: String
}

/**
 * Information, required to create a library item.
 *
 * Direct implementation: [LibraryEntry][io.spine.internal.catalog.entry.LibraryEntry].
 */
interface LibraryNotation : VersionNotation {

    /**
     * Group and artifact of a library, seperated by a colon.
     *
     * For example: `io.spine:spine-core`.
     */
    val module: String
}

/**
 * Information, required to create a plugin item.
 *
 * Direct implementation: [PluginEntry][io.spine.internal.catalog.entry.PluginEntry].
 */
interface PluginNotation : LibraryNotation {

    /**
     * A unique name, by which a plugin is represented in both Gradle Plugin Portal
     * and in the project.
     *
     * For example: `org.jetbrains.kotlin.jvm`.
     */
    val id: String
}

/**
 * Information, required to create a bundle item.
 *
 * This notation doesn't have a direct implementation. It is used only to hold
 * the data.
 */
interface BundleNotation : CatalogNotation {

    /**
     * Set of libraries, to be referenced as a whole.
     */
    val bundle: Set<LibraryNotation>
}
