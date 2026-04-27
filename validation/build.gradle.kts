/*
 * Copyright 2026, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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

import groovy.util.Node
import io.spine.dependency.local.Compiler
import io.spine.dependency.local.Validation
import io.spine.gradle.publish.addSourceAndDocJars

plugins {
    protobuf
    module
    `maven-publish`
    id(coreJvmCompiler.pluginId)
}

group = "io.spine.tools"

dependencies {
    implementation(Compiler.jvm)
    implementation(Validation.javaBundle)
    implementation(project(":time"))
}

configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("io.spine:spine-time"))
            .using(project(":time"))
        substitute(module("io.spine:spine-time-java"))
            .using(project(":time-java"))
        substitute(module("io.spine:spine-time-kotlin"))
            .using(project(":time-kotlin"))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            // `groupId`, `artifactId` and `version` are filled in by `CustomPublicationHandler`.
            artifact(tasks.named<Jar>("jar"))
            // Declare no dependencies because they are going to be available via the
            // classpath of the Validation Java plugin to which this module is plugged in
            // via the `ValidationOption` service API.
            pom.withXml {
                Node(asNode(), "dependencies")
            }
            addSourceAndDocJars(project)
        }
    }
}

spineCompilerRemoteDebug(enabled = false)
