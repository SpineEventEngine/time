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

/**
 * [GoogleApis](https://github.com/googleapis/)
 */
@Suppress("unused")
internal object GoogleApis : CatalogEntry() {

    /**
     * [Client](https://github.com/googleapis/google-api-java-client)
     */
    object Client : CatalogEntry() {
        override val version = "1.32.2"
        override val module = "com.google.api-client:google-api-client"
    }

    /**
     * [Common](https://github.com/googleapis/api-common-java)
     */
    object Common : CatalogEntry() {
        override val version = "2.1.1"
        override val module = "com.google.api:api-common"
    }

    /**
     * [CommonProtos](https://github.com/googleapis/java-common-protos)
     */
    object CommonProtos : CatalogEntry() {
        override val version = "2.7.0"
        override val module = "com.google.api.grpc:proto-google-common-protos"
    }

    /**
     * [GAX](https://github.com/googleapis/gax-java)
     */
    object Gax : CatalogEntry() {
        override val version = "2.7.1"
        override val module = "com.google.api:gax"
    }

    /**
     * [ProtoAim](https://github.com/googleapis/java-iam)
     */
    object ProtoAim : CatalogEntry() {
        override val version = "1.2.0"
        override val module = "com.google.api.grpc:proto-google-iam-v1"
    }

    /**
     * [OAuthClient](https://github.com/googleapis/google-oauth-java-client)
     */
    object OAuthClient : CatalogEntry() {
        override val version = "1.32.1"
        override val module = "com.google.oauth-client:google-oauth-client"
    }

    /**
     * [AuthLibrary](https://github.com/googleapis/google-auth-library-java)
     */
    object AuthLibrary : CatalogEntry() {
        override val version = "1.3.0"
        val credentials by lib("com.google.auth:google-auth-library-credentials")
        val oAuth2Http by lib("com.google.auth:google-auth-library-oauth2-http")
    }
}
