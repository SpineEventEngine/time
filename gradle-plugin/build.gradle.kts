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

import io.spine.dependency.local.ToolBase
import io.spine.gradle.publish.addSourceAndDocJars
import org.gradle.api.publish.maven.MavenPublication

plugins {
    module
    id("io.spine.artifact-meta")
    `java-gradle-plugin`
}

group = "io.spine.tools"

val moduleArtifactId = "time-gradle-plugin"

val versionToPublish: String by extra

artifactMeta {
    artifactId.set(moduleArtifactId)
    addDependencies(
        "io.spine:spine-time:$versionToPublish",
        "io.spine:spine-time-java:$versionToPublish",
        "io.spine:spine-time-kotlin:$versionToPublish",
    )
    excludeConfigurations {
        containing(*buildToolConfigurations)
    }
}

gradlePlugin {
    website.set("https://spine.io/")
    vcsUrl.set("https://github.com/SpineEventEngine/time.git")
    plugins {
        val pluginTags = listOf(
            "time",
            "ddd",
            "java",
            "kotlin",
            "jvm"
        )
        create("spineTime") {
            id = "io.spine.time"
            implementationClass = "io.spine.tools.time.gradle.TimeGradlePlugin"
            displayName = "Spine Time Gradle Plugin"
            description = "Configures the Spine Time library in a Gradle project."
            tags.set(pluginTags)
        }
    }
}

dependencies {
    compileOnly(gradleKotlinDsl())
    implementation(ToolBase.jvmTools)
}

afterEvaluate {
    publishing {
        publications {
            named<MavenPublication>("pluginMaven") {
                artifactId = moduleArtifactId
                addSourceAndDocJars(project)
            }
        }
    }
    val sourcesJar by tasks.getting(Jar::class)
    val writeArtifactMeta by tasks.getting
    sourcesJar.dependsOn(writeArtifactMeta)
}
