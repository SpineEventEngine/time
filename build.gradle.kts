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

@file:Suppress("RemoveRedundantQualifierName")

import io.spine.internal.dependency.Dokka
import io.spine.internal.dependency.JUnit
import io.spine.internal.dependency.Jackson
import io.spine.internal.dependency.Spine
import io.spine.internal.dependency.Validation
import io.spine.internal.gradle.forceVersions
import io.spine.internal.gradle.publish.PublishingRepos
import io.spine.internal.gradle.publish.spinePublishing
import io.spine.internal.gradle.report.coverage.JacocoConfig
import io.spine.internal.gradle.report.license.LicenseReporter
import io.spine.internal.gradle.report.pom.PomGenerator
import io.spine.internal.gradle.standardToSpineSdk

buildscript {
    standardSpineSdkRepositories()
    io.spine.internal.gradle.doForceVersions(configurations)

    dependencies {
        classpath(io.spine.internal.dependency.Spine.McJava.pluginLib)
    }

    val spine = io.spine.internal.dependency.Spine
    val jackson = io.spine.internal.dependency.Jackson
    configurations {
        all {
            resolutionStrategy {
                force(
                    spine.base,
                    jackson.annotations,
                    jackson.bom,
                    jackson.databind,
                    jackson.moduleKotlin
                )
            }
        }
    }
}

repositories {
    // Required to grab the dependencies for `JacocoConfig`.
    standardToSpineSdk()
}

plugins {
    idea
    jacoco
    `gradle-doctor`
    `project-report`
}

spinePublishing {
    modules = setOf(
        "time",
        "testutil-time"
    )
    destinations = with(PublishingRepos) {
        setOf(
            gitHub("time"),
            cloudRepo,
            cloudArtifactRegistry
        )
    }

    dokkaJar {
        kotlin = true
        java = true
    }
}

allprojects {
    apply(from = "$rootDir/version.gradle.kts")

    group = "io.spine"
    version = extra["versionToPublish"]!!

    repositories.standardToSpineSdk()

    configurations {
        forceVersions()
        all {
            exclude("io.spine:spine-validate")
            resolutionStrategy {
                force(
                    Spine.base,
                    Spine.toolBase,
                    Validation.runtime,
                    Dokka.BasePlugin.lib,
                    Jackson.databind,
                    JUnit.runner,
                )
            }
        }
    }
}

gradle.projectsEvaluated {
    JacocoConfig.applyTo(project)
    LicenseReporter.mergeAllReports(project)
    PomGenerator.applyTo(project)
}
