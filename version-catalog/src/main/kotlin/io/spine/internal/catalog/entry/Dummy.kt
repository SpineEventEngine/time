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

import io.spine.internal.catalog.LibraryEntry
import io.spine.internal.catalog.PluginEntry

/**
 * This entry just showcases the proposed API of dependency declarations.
 */
@Suppress("unused")
internal object Dummy : LibraryEntry() {

    private const val group = "org.dummy.company"
    override val module = "$group:dummy-lib" // libs.dummy
    override val version = "1.0.0"           // libs.versions.dummy

    val core by lib("$group:dummy-core")     // libs.dummy.core
    val runner by lib("$group:dummy-runner") // libs.dummy.runner
    val api by lib("$group:dummy-api")       // libs.dummy.api

    // In bundles, you can reference already declared libs,
    // or create them in-place.

    override val bundle = setOf(                          // libs.bundles.dummy
        core, runner, api,
        lib("params","$group:dummy-params"), // libs.dummy.params
        lib("types","$group:dummy-types"),   // libs.dummy.types
    )

    object GradlePlugin : PluginEntry() {
        override val version = "0.0.8"                 // libs.versions.dummy.gradlePlugin
        override val module = "$group:my-dummy-plugin" // libs.dummy.gradlePlugin
        override val id = "my-dummy-plugin"            // libs.plugins.dummy
    }

    object Runtime : LibraryEntry() {

        // When the version is not overridden, it is taken from the outer entry.
        // In this case, all libs within "Runtime" entry will have "1.0.0".

        val win by lib("$group:runtime-win")     // libs.dummy.runtime.win
        val mac by lib("$group:runtime-mac")     // libs.dummy.runtime.mac
        val linux by lib("$group:runtime-linux") // libs.dummy.runtime.linux

        object BOM : LibraryEntry() {
            override val version = "2.0.0"           // libs.versions.dummy.runtime.bom
            override val module = "$group:dummy-bom" // libs.dummy.runtime.bom
        }
    }

    // The lib declared as `LibraryEntry` can be referenced as well
    // as the one declared by `lib()` delegate.

    val runtime by bundle( // libs.bundles.dummy.runtime
        Runtime.BOM,
        Runtime.win,
        Runtime.mac,
        Runtime.linux,
    )
}

/**
 * The code below is for verification. Further, it is better to
 * implement a test. As for now, it can be run in order to check if
 * API implementation works correctly.
 */

/*
val dummyEntries = with(libs) {
    listOf(
        versions.dummy,
        dummy,
        "",
        dummy.core,
        dummy.runner,
        dummy.api,
        "",
        bundles.dummy,
        dummy.params,
        dummy.types,
        "",
        versions.dummy.gradlePlugin,
        dummy.gradlePlugin,
        plugins.dummy,
        "",
        dummy.runtime.win,
        dummy.runtime.mac,
        dummy.runtime.linux,
        "",
        versions.dummy.runtime.bom,
        dummy.runtime.bom,
        "",
        bundles.dummy.runtime,
    )
}

dummyEntries.map {
    when (it) {
        is ProviderConvertible<*> -> it.asProvider().get()
        is Provider<*> -> it.get()
        is String -> it
        else -> throw IllegalArgumentException(it.toString())
    }
}.forEach { println(it) }
 */
