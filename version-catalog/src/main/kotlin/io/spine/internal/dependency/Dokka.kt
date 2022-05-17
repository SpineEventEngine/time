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

import io.spine.internal.version.catalog.SpineVersionCatalogBuilder
import io.spine.internal.version.catalog.VersionCatalogContributor

@Suppress("unused")
internal object Dokka : VersionCatalogContributor() {

    /**
     * [Dokka](https://github.com/Kotlin/dokka)
     */
    private const val version = "1.6.20"
    private const val spineExtVersion = "2.0.0-SNAPSHOT.3"
    private const val group = "org.jetbrains.dokka"

    override fun SpineVersionCatalogBuilder.doContribute() {

        /**
         * The version of this plugin is already specified in `buildSrc/build.gradle.kts` file.
         * Thus, when applying the plugin in project's build files, only id should be used.
         */
        plugin("org.jetbrains.dokka", version)

        library("gradlePlugin", "${group}:dokka-gradle-plugin:${version}")
        library("basePlugin", "${group}:dokka-base:${version}")

        /**
         * To generate the documentation as seen from Java perspective use this plugin.
         *
         * @see <a href="https://github.com/Kotlin/dokka#output-formats">
         *     Dokka output formats</a>
         */
        library("kotlinAsJavaPlugin", "${group}:kotlin-as-java-plugin:${version}")

        /**
         * Custom Dokka plugins developed for Spine-specific needs like excluding by `@Internal`
         * annotation.
         *
         * @see <a href="https://github.com/SpineEventEngine/dokka-tools/tree/master/dokka-extensions">
         *     Custom Dokka Plugins</a>
         */
        library("spineExtensions", "io.spine.tools:spine-dokka-extensions:${spineExtVersion}")
    }
}
