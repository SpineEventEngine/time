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

package io.spine.internal.catalog.entries

import io.spine.internal.catalog.entry.DependencyEntry
import io.spine.internal.catalog.entry.LibraryEntry
import io.spine.internal.catalog.entry.PluginEntry
import io.spine.internal.catalog.entry.VersionEntry

/**
 * This dependency describes an imaginary library.
 *
 * It is used to showcase API for dependency declaration and perform true
 * functional testing.
 *
 * Side comments to certain statements demonstrate how those lines will
 * be represented in the generated type-safe accessors.
 *
 * @see `io.spine.internal.catalog.SpineVersionCatalogFunctionalTest`
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
internal object Dummy : DependencyEntry() {

    private const val group = "org.dummy.company"
    override val module = "$group:dummy-lib" // libs.dummy
    override val version = "1.0.0"           // libs.versions.dummy

    val core by lib("$group:dummy-core")     // libs.dummy.core
    val runner by lib("$group:dummy-runner") // libs.dummy.runner
    val api by lib("$group:dummy-api")       // libs.dummy.api

    // In bundles, you can reference already declared libs,
    // or create them in-place.

    override val bundle = setOf( // libs.bundles.dummy
        core, runner, api,
        lib("params", "$group:dummy-params"), // libs.dummy.params
        lib("types", "$group:dummy-types"),   // libs.dummy.types
    )

    // "GradlePlugin" - is a special entry name for `PluginEntry`.
    // For plugin entries with this name, the facade will not put "gradlePlugin"
    // suffix for a plugin's id. Note, that we have this suffix for the version
    // and module, and does not have for id.

    object GradlePlugin : PluginEntry() {
        override val version = "0.0.8"                 // libs.versions.dummy.gradlePlugin
        override val module = "$group:my-dummy-plugin" // libs.dummy.gradlePlugin
        override val id = "my-dummy-plugin"            // libs.plugins.dummy
    }

    object Runtime : DependencyEntry() {

        // When an entry does not override the version, it is taken from
        // the outer entry. For example, in this case, all libs within "Runtime"
        // entry will have "1.0.0".

        val win by lib("$group:runtime-win")     // libs.dummy.runtime.win
        val mac by lib("$group:runtime-mac")     // libs.dummy.runtime.mac
        val linux by lib("$group:runtime-linux") // libs.dummy.runtime.linux

        object BOM : LibraryEntry() {
            override val version = "2.0.0"           // libs.versions.dummy.runtime.bom
            override val module = "$group:dummy-bom" // libs.dummy.runtime.bom
        }
    }

    // A library that is declared as `object SomeLib : LibraryEntry()` can be
    // referenced as well as the one declared by `lib()` delegate.

    val runtime by bundle( // libs.bundles.dummy.runtime
        Runtime.BOM,
        Runtime.win,
        Runtime.mac,
        Runtime.linux,
    )

    // It is also possible to declare just a bare version.

    object Tools : VersionEntry() {
        override val version = "3.0.0" // libs.versions.dummy.tools
    }
}
