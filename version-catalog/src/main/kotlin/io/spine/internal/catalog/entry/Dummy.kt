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

@Suppress("unused", "MemberVisibilityCanBePrivate")
internal object Dummy : LibraryEntry() {

    private const val group = "org.dummy.company"
    override val module = "${io.spine.internal.catalog.entry.Dummy.group}:dummy-lib"
    override val version = "1.0.0"

    val core by module("${io.spine.internal.catalog.entry.Dummy.group}:dummy-core")
    val runner by module("${io.spine.internal.catalog.entry.Dummy.group}:dummy-runner")
    val api by module("${io.spine.internal.catalog.entry.Dummy.group}:dummy-api")

    override val bundle = setOf(
        io.spine.internal.catalog.entry.Dummy.core,
        io.spine.internal.catalog.entry.Dummy.runner,
        io.spine.internal.catalog.entry.Dummy.api
    )

    object GradlePlugin : PluginEntry() {
        override val version = "0.0.8"
        override val module = "${io.spine.internal.catalog.entry.Dummy.group}:my-dummy-plugin"
        override val id = "my-dummy-plugin"
    }
}
