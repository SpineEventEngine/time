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

/**
 * [Grpc Java](https://github.com/grpc/grpc-java)
 */
@Suppress("unused")
internal object Grpc : DependencyEntry() {
    override val version = "1.45.1"
    val api by lib("io.grpc:grpc-api")
    val auth by lib("io.grpc:grpc-auth")
    val core by lib("io.grpc:grpc-core")
    val context by lib("io.grpc:grpc-context")
    val stub by lib("io.grpc:grpc-stub")
    val okHttp by lib("io.grpc:grpc-okhttp")
    val protobuf by lib("io.grpc:grpc-protobuf")
    val protobufLite by lib("io.grpc:grpc-protobuf-lite")
    val protobufPlugin by lib("io.grpc:protoc-gen-grpc-java")
    val netty by lib("io.grpc:grpc-netty")
    val nettyShaded by lib("io.grpc:grpc-netty-shaded")
}
