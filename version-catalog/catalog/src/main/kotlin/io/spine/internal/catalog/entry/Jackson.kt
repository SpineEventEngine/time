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

import io.spine.internal.catalog.entry.DependencyEntry
import io.spine.internal.catalog.entry.LibraryEntry

@Suppress("unused")
internal object Jackson : DependencyEntry() {

    override val version = "2.13.2"

    /**
     * [Core](https://github.com/FasterXML/jackson-core)
     */
    val core by lib("com.fasterxml.jackson.core:jackson-core")

    /**
     * [DataformatXml](https://github.com/FasterXML/jackson-dataformat-xml/releases)
     */
    val dataformatXml by lib("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")

    /**
     * [DataformatYaml](https://github.com/FasterXML/jackson-dataformats-text/releases)
     */
    val dataformatYaml by lib("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    /**
     * [ModuleKotlin](https://github.com/FasterXML/jackson-module-kotlin/releases)
     */
    val moduleKotlin by lib("com.fasterxml.jackson.module:jackson-module-kotlin")

    /**
     * [Databind](https://github.com/FasterXML/jackson-databind)
     */
    object Databind : LibraryEntry() {
        override val version = "2.13.2.2"
        override val module = "com.fasterxml.jackson.core:jackson-databind"
    }
}
