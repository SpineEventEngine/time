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

package io.spine.internal.dependency

import io.spine.internal.version.catalog.VersionCatalogEntry

/**
 * [ErrorProne](https://github.com/google/error-prone)
 */
@Suppress("unused")
internal object ErrorProne : VersionCatalogEntry() {

    private const val version = "2.13.1"
    val core by gav("com.google.errorprone:error_prone_core:$version")
    val checkApi by gav("com.google.errorprone:error_prone_check_api:$version")
    val testHelpers by gav("com.google.errorprone:error_prone_test_helpers:$version")

    val annotations by libs(
        lib("annotations", "com.google.errorprone:error_prone_annotations:$version"),
        lib("typeAnnotations", "com.google.errorprone:error_prone_type_annotations:$version")
    )

    /**
     * [JavacPlugin](https://github.com/tbroyer/gradle-errorprone-plugin/blob/v0.8/build.gradle.kts)
     */
    object JavacPlugin : VersionCatalogEntry() {
        private const val version = "9+181-r4173-1"
        val lib by gav("com.google.errorprone:javac:$version")
    }

    /**
     * [GradlePlugin](https://github.com/tbroyer/gradle-errorprone-plugin/releases)
     */
    object GradlePlugin : VersionCatalogEntry() {
        private const val version = "2.0.2"
        val plugin by id("net.ltgt.errorprone", version)
        val lib by gav("net.ltgt.gradle:gradle-errorprone-plugin:$version")
    }
}