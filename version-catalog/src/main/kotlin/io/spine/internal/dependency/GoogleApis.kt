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

import io.spine.internal.version.catalog.VersionCatalogEntryOld

/**
 * [GoogleApis](https://github.com/googleapis/).
 */
@Suppress("unused")
internal object GoogleApis : VersionCatalogEntryOld() {

    /**
     * [Client](https://github.com/googleapis/google-api-java-client).
     */
    val client by lib("com.google.api-client:google-api-client:1.32.2")

    /**
     * [Common](https://github.com/googleapis/api-common-java).
     */
    val common by lib("com.google.api:api-common:2.1.1")

    /**
     * [CommonProtos](https://github.com/googleapis/java-common-protos).
     */
    val commonProtos by lib("com.google.api.grpc:proto-google-common-protos:2.7.0")

    /**
     * [GAX](https://github.com/googleapis/gax-java).
     */
    val gax by lib("com.google.api:gax:2.7.1")

    /**
     * [ProtoAim](https://github.com/googleapis/java-iam).
     */
    val protoAim by lib("com.google.api.grpc:proto-google-iam-v1:1.2.0")

    /**
     * [OAuthClient](https://github.com/googleapis/google-oauth-java-client).
     */
    val oAuthClient by lib("com.google.oauth-client:google-oauth-client:1.32.1")

    /**
     * [AuthLibrary](https://github.com/googleapis/google-auth-library-java).
     */
    object AuthLibrary : VersionCatalogEntryOld() {
        private const val version = "1.3.0"
        val authLibrary by versioning(version)
        val credentials by lib("com.google.auth:google-auth-library-credentials:$version")
        val oAuth2Http by lib("com.google.auth:google-auth-library-oauth2-http:$version")
    }
}
