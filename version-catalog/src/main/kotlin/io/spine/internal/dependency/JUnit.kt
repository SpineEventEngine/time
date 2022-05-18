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
 * [JUnit5](https://junit.org/junit5/).
 */
@Suppress("unused")
internal object JUnit : VersionCatalogEntry() {
    private const val version = "5.8.2"
    private const val platformVersion = "1.8.2"
    private const val legacyVersion = "4.13.1"

    object Versions {
        val junit by version(version)
    }

    /**
     * [ApiGuardian](https://github.com/apiguardian-team/apiguardian).
     */
    private const val apiGuardianVersion = "1.1.2"

    /**
     * [Junit-Pioneer](https://github.com/junit-pioneer/junit-pioneer).
     */
    private const val pioneerVersion = "1.5.0"

    val legacy by lib("junit:junit:$legacyVersion")
    val bom by lib("org.junit:junit-bom:$version")
    val runner by lib("org.junit.jupiter:junit-jupiter-engine:$version")
    val pioneer by lib("org.junit-pioneer:junit-pioneer:$pioneerVersion")
    val platformCommons by lib("org.junit.platform:junit-platform-commons:$platformVersion")
    val platformLauncher by lib("org.junit.platform:junit-platform-launcher:$platformVersion")

    val params by lib("org.junit.jupiter:junit-jupiter-params:$version")
    val apiGuardian by lib("org.apiguardian:apiguardian-api:$apiGuardianVersion")
    val api by lib("org.junit.jupiter:junit-jupiter-api:$version")

    object Bundle {
        val api by bundle(params, apiGuardian, JUnit.api)
    }
}
