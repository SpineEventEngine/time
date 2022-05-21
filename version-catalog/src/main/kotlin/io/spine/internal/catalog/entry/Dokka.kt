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

import io.spine.internal.version.catalog.VersionCatalogEntryOld

/**
 * [Dokka](https://github.com/Kotlin/dokka).
 */
@Suppress("unused")
internal object Dokka : VersionCatalogEntryOld() {

    private const val version = "1.6.20"
    private const val group = "org.jetbrains.dokka"

    /**
     * To generate the documentation as seen from Java perspective use this plugin.
     *
     * @see <a href="https://github.com/Kotlin/dokka#output-formats">
     *     Dokka output formats</a>
     */
    val kotlinAsJavaPlugin by lib("${io.spine.internal.catalog.entry.Dokka.group}:kotlin-as-java-plugin:${io.spine.internal.catalog.entry.Dokka.version}")
    val basePlugin by lib("${io.spine.internal.catalog.entry.Dokka.group}:dokka-base:${io.spine.internal.catalog.entry.Dokka.version}")

    /**
     * Custom Dokka plugins developed for Spine-specific needs like excluding by `@Internal`
     * annotation.
     *
     * @see <a href="https://github.com/SpineEventEngine/dokka-tools/tree/master/dokka-extensions">
     *     Custom Dokka Plugins</a>
     */
    object SpineExtensions {
        private const val group = "io.spine.tools"
        private const val version = "2.0.0-SNAPSHOT.3"
        val spineExtensions by lib("${io.spine.internal.catalog.entry.Dokka.SpineExtensions.group}:spine-dokka-extensions:${io.spine.internal.catalog.entry.Dokka.SpineExtensions.version}")
    }

    object GradlePlugin {
        val dokka by plugin("org.jetbrains.dokka", io.spine.internal.catalog.entry.Dokka.version)
        val gradlePlugin by lib("${io.spine.internal.catalog.entry.Dokka.group}:dokka-gradle-plugin:${io.spine.internal.catalog.entry.Dokka.version}")
    }
}
