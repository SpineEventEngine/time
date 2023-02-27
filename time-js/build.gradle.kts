/*
 * Copyright 2023, TeamDev. All rights reserved.
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

import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.remove

plugins {
    protobuf
}

val timeProject = project(":time")

dependencies {
    implementation(timeProject)
}

sourceSets {
    main {
        proto.srcDir(timeProject.projectDir.resolve("src/main/proto"))
    }
}

protobuf {
    generatedFilesBaseDir = "$projectDir/generated"
    protoc {
        // Temporarily use this version, since 3.21.x is known to provide
        // a broken `protoc-gen-js` artifact.
        // See https://github.com/protocolbuffers/protobuf-javascript/issues/127.
        //
        // Once it is addressed, this artifact should be `Protobuf.compiler`.
        artifact = "com.google.protobuf:protoc:3.19.6"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                // Do not use java builtin output in this project.
                remove("java")

                id("js") {
                    option("library=spine-time-${project.project.version}")
                    outputSubDir = "js"
                }
            }
        }
    }
}
