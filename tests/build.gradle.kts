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

@file:Suppress("RemoveRedundantQualifierName") // To prevent IDEA replacing FQN imports.

import io.spine.dependency.boms.BomsPlugin
import io.spine.dependency.kotlinx.Coroutines
import io.spine.dependency.lib.Grpc
import io.spine.dependency.lib.Jackson
import io.spine.dependency.lib.Kotlin
import io.spine.dependency.local.Base
import io.spine.dependency.local.Compiler
import io.spine.dependency.local.Logging
import io.spine.dependency.local.Reflect
import io.spine.dependency.local.Time
import io.spine.dependency.local.Validation
import io.spine.gradle.kotlin.applyJvmToolchain
import io.spine.gradle.kotlin.setFreeCompilerArgs
import io.spine.gradle.publish.PublishingRepos.gitHub
import io.spine.gradle.repo.standardToSpineSdk

buildscript {
    standardSpineSdkRepositories()
    doForceVersions(configurations)

    apply(from = "${rootDir}/../version.gradle.kts")
    val versionToPublish = extra["versionToPublish"].toString()
    dependencies {
        classpath(io.spine.dependency.local.Validation.gradlePluginLib)
        classpath(io.spine.dependency.local.Time.gradlePlugin(versionToPublish))
    }

    configurations {
        all {
            exclude(group = "io.spine", module = "spine-flogger-api")
            exclude(group = "io.spine", module = "spine-logging-backend")

            resolutionStrategy {
                val jackson = io.spine.dependency.lib.Jackson
                val cfg = this@all
                val rs = this@resolutionStrategy
                jackson.forceArtifacts(project, cfg, rs)
                io.spine.dependency.lib.Jackson.DataType.forceArtifacts(project, cfg, rs)

                io.spine.dependency.kotlinx.Coroutines.forceArtifacts(
                    project, this@all, this@resolutionStrategy
                )
                io.spine.dependency.lib.Grpc.forceArtifacts(
                    project, this@all, this@resolutionStrategy
                )
                force(
                    io.spine.dependency.lib.Kotlin.bom,
                    io.spine.dependency.lib.Jackson.annotations,
                    io.spine.dependency.lib.Grpc.bom,
                    io.spine.dependency.local.Base.annotations,
                    io.spine.dependency.local.Base.environment,
                    io.spine.dependency.local.Base.lib,
                    io.spine.dependency.local.Reflect.lib,
                    io.spine.dependency.local.Time.lib(versionToPublish),
                    io.spine.dependency.local.Time.javaExtensions(versionToPublish),
                    io.spine.dependency.local.Logging.lib,
                    io.spine.dependency.local.Logging.middleware,
                    io.spine.dependency.local.Validation.runtime,
                    io.spine.dependency.local.Compiler.api,
                    io.spine.dependency.local.Compiler.gradleApi,
                    io.spine.dependency.local.Compiler.params,
                    io.spine.dependency.local.Compiler.pluginLib,
                )
            }
        }
    }
}

plugins {
    kotlin("jvm")
    id("module-testing")
    `java-test-fixtures`
}
apply(plugin ="io.spine.validation")
apply(plugin ="io.spine.time")

apply(from = "${rootDir}/../version.gradle.kts")

group = "io.spine.tools"
version = extra["versionToPublish"]!!

repositories {
    standardToSpineSdk()
    gitHub("time")
    mavenLocal()
}

apply<BomsPlugin>()

configurations {
    forceVersions()
    all {
        exclude(group = "io.spine", module = "spine-validate")
        exclude(group = "io.spine", module = "spine-flogger-api")
        resolutionStrategy {
            val cfg = this@all
            val rs = this@resolutionStrategy
            Jackson.forceArtifacts(project, cfg, rs)
            Jackson.DataType.forceArtifacts(project, cfg, rs)
            Jackson.DataFormat.forceArtifacts(project, cfg, rs)
            Coroutines.forceArtifacts(project, cfg, rs)
            Grpc.forceArtifacts(project, cfg, rs)
            Kotlin.StdLib.forceArtifacts(project, cfg, rs)
            force(
                Kotlin.bom,
                Jackson.annotations,
                Grpc.bom,
                Reflect.lib,
                Base.lib,
                Logging.lib,
                Logging.middleware,
                Logging.testLib,
                Validation.runtime,
                Compiler.api,
                Time.lib,
                Time.javaExtensions,
            )
        }
    }
}

kotlin {
    applyJvmToolchain(BuildSettings.javaVersion.asInt())
    compilerOptions {
        jvmTarget.set(BuildSettings.jvmTarget)
        setFreeCompilerArgs()
    }
}

dependencies {
    testFixturesImplementation(Time.lib(version.toString()))
    testFixturesImplementation(Validation.runtime)
    testFixturesImplementation(Compiler.api)
    testFixturesImplementation(Compiler.testlib)
}
