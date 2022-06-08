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

import io.spine.internal.catalog.model.CatalogEntry

@Suppress("unused")
internal object GoogleCloud : CatalogEntry() {

    /**
     * [Core](https://github.com/googleapis/java-core)
     */
    object Core : CatalogEntry() {
        override val version = "2.3.3"
        override val module = "com.google.cloud:google-cloud-core"
    }

    /**
     * [PubSubGrcpApi](https://github.com/googleapis/java-pubsub/tree/main/proto-google-cloud-pubsub-v1)
     */
    object PubSubGrpcApi : CatalogEntry() {
        override val version = "1.97.0"
        override val module = "com.google.api.grpc:proto-google-cloud-pubsub-v1"
    }

    /**
     * [Trace](https://github.com/googleapis/java-trace)
     */
    object Trace : CatalogEntry() {
        override val version = "2.1.0"
        override val module = "com.google.cloud:google-cloud-trace"
    }

    /**
     * [Datastore](https://github.com/googleapis/java-datastore)
     */
    object Datastore : CatalogEntry() {
        override val version = "2.2.1"
        override val module = "com.google.cloud:google-cloud-datastore"
    }

    object ArtifactRegistry : CatalogEntry() {
        override val version = "2.1.2"
        val authCommon by lib("com.google.cloud.artifactregistry:artifactregistry-auth-common")
    }
}
