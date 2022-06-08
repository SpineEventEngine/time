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

import io.spine.internal.catalog.model.DependencyEntry
import io.spine.internal.catalog.model.LibraryEntry
import io.spine.internal.catalog.model.PluginEntry

/**
 * [ErrorProne](https://github.com/google/error-prone)
 */
@Suppress("unused")
internal object ErrorProne : DependencyEntry() {

    override val version = "2.13.1"

    val core by lib("com.google.errorprone:error_prone_core")
    val checkApi by lib("com.google.errorprone:error_prone_check_api")
    val testHelpers by lib("com.google.errorprone:error_prone_test_helpers")

    val annotations by bundle(
        lib("annotations", "com.google.errorprone:error_prone_annotations"),
        lib("typeAnnotations", "com.google.errorprone:error_prone_type_annotations")
    )

    /**
     * [JavacPlugin](https://github.com/tbroyer/gradle-errorprone-plugin/blob/v0.8/build.gradle.kts)
     */
    object JavacPlugin : LibraryEntry() {
        override val version = "9+181-r4173-1"
        override val module = "com.google.errorprone:javac"
    }

    /**
     * [GradlePlugin](https://github.com/tbroyer/gradle-errorprone-plugin/releases)
     */
    object GradlePlugin : PluginEntry() {
        override val version = "2.0.2"
        override val module = "net.ltgt.gradle:gradle-errorprone-plugin"
        override val id = "net.ltgt.errorprone"
    }
}
