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

package io.spine.internal.catalog.model.given

import io.spine.internal.catalog.model.DependencyEntry

internal object StandaloneDummyVersionDependency : DependencyEntry() {
    override val version = "sdvd-0.0.1"
}

internal object StandaloneDummyLibraryDependency : DependencyEntry() {
    override val version = "sdld-0.0.1"
    override val module = "org.dummy"
}

internal object StandaloneDummyBundleDependency : DependencyEntry() {
    override val version = "sdbd-0.0.1"
    override val bundle = setOf(
        lib("subLib1", "org.dummy:subLib1"),
        lib("subLib2", "org.dummy:subLib2"),
        lib("subLib3", "org.dummy:subLib3"),
    )
}

@Suppress("unused")
internal object StandaloneDummyPropertyDependency : DependencyEntry() {
    override val version = "sdpd-0.0.1"
    val subLib by lib("org.dummy:subLib")
}

@Suppress("unused")
internal object SimpleDependency : DependencyEntry() {
    override val version = "sdpnd-0.0.1"
    val simpleDependency by lib("org.simple")
}

internal object OuterDummyDependency : DependencyEntry() {
    override val version = "odp-0.0.1"

    object Nested : DependencyEntry() {
        override val module = "org.nested"
    }
}

internal object ErroneousOuterDummyDependency : DependencyEntry() {
    object Nested : DependencyEntry() {
        override val module = "org.dummy"
    }
}
