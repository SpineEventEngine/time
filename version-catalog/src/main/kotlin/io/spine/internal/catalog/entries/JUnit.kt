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

/**
 * [JUnit5](https://junit.org/junit5/)
 */
@Suppress("unused")
internal object JUnit : DependencyEntry() {

    override val version = "5.8.2"
    val bom by lib("org.junit:junit-bom")
    val runner by lib("org.junit.jupiter:junit-jupiter-engine")

    /**
     * [ApiGuardian](https://github.com/apiguardian-team/apiguardian)
     */
    object ApiGuardian : LibraryEntry() {
        override val version = "1.1.2"
        override val module = "org.apiguardian:apiguardian-api"
    }

    object Platform : DependencyEntry() {
        override val version = "1.8.2"
        val commons by lib("org.junit.platform:junit-platform-commons")
        val launcher by lib("org.junit.platform:junit-platform-launcher")
    }

    object Legacy : LibraryEntry() {
        override val version = "4.13.1"
        override val module = "junit:junit"
    }

    /**
     * [Junit-Pioneer](https://github.com/junit-pioneer/junit-pioneer)
     */
    object Pioneer : LibraryEntry() {
        override val version = "1.5.0"
        override val module = "org.junit-pioneer:junit-pioneer"
    }

    override val bundle = setOf(
        lib("api", "org.junit.jupiter:junit-jupiter-api"),
        lib("params", "org.junit.jupiter:junit-jupiter-params"),
        ApiGuardian
    )
}