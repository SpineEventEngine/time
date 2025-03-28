/*
 * Copyright 2025, TeamDev. All rights reserved.
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

@file:Suppress("RemoveRedundantQualifierName")

import io.spine.dependency.build.Dokka
import io.spine.dependency.lib.Jackson
import io.spine.dependency.lib.KotlinPoet
import io.spine.dependency.lib.KotlinX
import io.spine.dependency.local.Base
import io.spine.dependency.local.Logging
import io.spine.dependency.local.ProtoData
import io.spine.dependency.local.Reflect
import io.spine.dependency.local.ToolBase
import io.spine.dependency.local.Validation
import io.spine.dependency.test.JUnit
import io.spine.gradle.publish.PublishingRepos
import io.spine.gradle.publish.spinePublishing
import io.spine.gradle.report.coverage.JacocoConfig
import io.spine.gradle.report.license.LicenseReporter
import io.spine.gradle.report.pom.PomGenerator
import io.spine.gradle.standardToSpineSdk

buildscript {
    standardSpineSdkRepositories()
    doForceVersions(configurations)

    dependencies {
        classpath(io.spine.dependency.local.McJava.pluginLib)
    }

    configurations {
        all {
            exclude(group = "io.spine", module = "spine-flogger-api")
            exclude(group = "io.spine", module = "spine-logging-backend")

            resolutionStrategy {
                val validation = io.spine.dependency.local.Validation
                val jackson = io.spine.dependency.lib.Jackson
                val logging = io.spine.dependency.local.Logging
                force(
                    io.spine.dependency.lib.KotlinX.Coroutines.bom,
                    io.spine.dependency.lib.KotlinX.Coroutines.core,
                    io.spine.dependency.lib.KotlinX.Coroutines.coreJvm,
                    io.spine.dependency.lib.KotlinX.Coroutines.jdk8,
                    io.spine.dependency.local.Base.lib,
                    io.spine.dependency.local.Reflect.lib,
                    logging.lib,
                    logging.middleware,
                    validation.runtime,
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
        "time-testlib"
    )
    destinations = with(PublishingRepos) {
        setOf(
            gitHub("time"),
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
            exclude(group = "io.spine", module = "spine-validate")
            exclude(group = "io.spine", module = "spine-flogger-api")
            resolutionStrategy {
                force(
                    KotlinPoet.lib,
                    KotlinX.Coroutines.bom,
                    KotlinX.Coroutines.core,
                    KotlinX.Coroutines.coreJvm,
                    KotlinX.Coroutines.debug,
                    KotlinX.Coroutines.jdk8,
                    KotlinX.Coroutines.test,
                    KotlinX.Coroutines.testJvm,
                    Reflect.lib,
                    Base.lib,
                    ToolBase.lib,
                    Logging.lib,
                    Logging.middleware,
                    ProtoData.api,
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
