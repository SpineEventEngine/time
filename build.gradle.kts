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

@file:Suppress("RemoveRedundantQualifierName")

import io.spine.dependency.build.Dokka
import io.spine.dependency.kotlinx.Coroutines
import io.spine.dependency.lib.Grpc
import io.spine.dependency.kotlinx.AtomicFu
import io.spine.dependency.lib.Protobuf
import io.spine.dependency.lib.Caffeine
import io.spine.dependency.lib.Jackson
import io.spine.dependency.lib.JacksonV2
import io.spine.dependency.lib.Kotlin
import io.spine.dependency.lib.KotlinPoet
import io.spine.dependency.local.Base
import io.spine.dependency.local.CoreJvm
import io.spine.dependency.local.Logging
import io.spine.dependency.local.Reflect
import io.spine.dependency.local.Time
import io.spine.dependency.local.ToolBase
import io.spine.dependency.local.Validation
import io.spine.gradle.RunBuild
import io.spine.gradle.publish.PublishingRepos
import io.spine.gradle.publish.SpinePublishing
import io.spine.gradle.publish.spinePublishing
import io.spine.gradle.repo.standardToSpineSdk
import io.spine.gradle.report.coverage.JacocoConfig
import io.spine.gradle.report.license.LicenseReporter
import io.spine.gradle.report.pom.PomGenerator
import java.time.Duration

buildscript {
    standardSpineSdkRepositories()
    doForceVersions(configurations)

    dependencies {
        classpath(io.spine.dependency.local.Compiler.pluginLib)
        classpath(io.spine.dependency.local.CoreJvmCompiler.gradlePlugin)
        classpath(io.spine.dependency.local.ToolBase.jvmToolPlugins)
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
                io.spine.dependency.lib.Jackson.DataFormat.forceArtifacts(project, cfg, rs)
                // The Jackson 2.x line (`com.fasterxml.*`) arrives through the
                // refresh-era plugin jars and floor artifacts; the helpers
                // above cover only the 3.x (`tools.jackson.*`) family.
                io.spine.dependency.lib.JacksonV2.Core.forceArtifacts(project, cfg, rs)
                io.spine.dependency.lib.JacksonV2.DataType.forceArtifacts(project, cfg, rs)
                io.spine.dependency.lib.JacksonV2.DataFormat.forceArtifacts(project, cfg, rs)
                io.spine.dependency.lib.JacksonV2.Module.forceArtifacts(project, cfg, rs)
                io.spine.dependency.lib.JacksonV2.Junior.forceArtifacts(project, cfg, rs)

                io.spine.dependency.kotlinx.Coroutines.forceArtifacts(
                    project, this@all, this@resolutionStrategy
                )
                io.spine.dependency.lib.Grpc.forceArtifacts(
                    project, this@all, this@resolutionStrategy
                )
                val validation = io.spine.dependency.local.Validation
                val logging = io.spine.dependency.local.Logging
                val toolBase = io.spine.dependency.local.ToolBase
                force(
                    io.spine.dependency.lib.Kotlin.bom,
                    io.spine.dependency.lib.Jackson.annotations,
                    io.spine.dependency.lib.Jackson.bom,
                    // Floor artifacts request the pre-refresh versions;
                    // the Protobuf runtime must never be older than the
                    // refreshed gencode.
                    io.spine.dependency.lib.Caffeine.lib,
                    io.spine.dependency.kotlinx.Coroutines.bom,
                    io.spine.dependency.kotlinx.AtomicFu.lib,
                    io.spine.dependency.lib.Protobuf.javaLib,
                    io.spine.dependency.lib.Grpc.bom,
                    io.spine.dependency.local.Base.annotations,
                    io.spine.dependency.local.Base.environment,
                    io.spine.dependency.local.Base.lib,
                    io.spine.dependency.local.Base.format,
                    io.spine.dependency.local.Reflect.lib,
                    io.spine.dependency.local.Time.lib,
                    io.spine.dependency.local.Time.javaExtensions,
                    logging.lib,
                    logging.middleware,
                    validation.runtime,
                    // `module.gradle.kts` forces the rest of the tool-base
                    // artifacts for project configurations; only the ones
                    // a failure proved necessary are repeated here.
                    // The published Compiler was built against the previous
                    // `tool-base` and requests its artifacts one version behind
                    // the one pinned here, which `failOnVersionConflict()`
                    // cannot resolve on its own. The two are source-identical:
                    // that release carried only build-script changes.
                    toolBase.archive,
                    toolBase.code,
                    toolBase.fs,
                    toolBase.javaCode,
                    toolBase.kotlinCode,
                    toolBase.protoCode,
                    toolBase.classicCodegen,
                    toolBase.pluginBase,
                    toolBase.pluginTestlib,
                    toolBase.intellijPlatform,
                    toolBase.intellijPlatformJava,
                    toolBase.psi,
                    toolBase.psiJava,
                    toolBase.rootGradlePlugins,
                    toolBase.gradlePluginApi,
                    toolBase.gradlePluginApiTestFixtures,
                    toolBase.jvmTools,
                    toolBase.jvmToolPlugins,
                    toolBase.protobufSetupPlugins,
                    io.spine.dependency.local.Compiler.api,
                    io.spine.dependency.local.Compiler.gradleApi,
                    io.spine.dependency.local.Compiler.params,
                    io.spine.dependency.local.Compiler.pluginLib,
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
    base
    id("org.jetbrains.kotlinx.kover")
    idea
    jacoco
    `project-report`
}

spinePublishing {
    artifactPrefix = "spine-"
    toolArtifactPrefix = "time-"
    modulesWithCustomPublishing = setOf(
        "gradle-plugin",
        "validation",
    )
    modules = productionModules.map { it.name }.toSet() - modulesWithCustomPublishing
    destinations = with(PublishingRepos) {
        setOf(
            gitHub("time"),
            cloudArtifactRegistry
        )
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
                val cfg = this@all
                val rs = this@resolutionStrategy
                Jackson.forceArtifacts(project, cfg, rs)
                Jackson.DataType.forceArtifacts(project, cfg, rs)
                // The Jackson 2.x line (`com.fasterxml.*`) arrives through the
                // refresh-era plugin jars and floor artifacts; the helpers
                // above cover only the 3.x (`tools.jackson.*`) family.
                JacksonV2.Core.forceArtifacts(project, cfg, rs)
                JacksonV2.DataType.forceArtifacts(project, cfg, rs)
                JacksonV2.DataFormat.forceArtifacts(project, cfg, rs)
                JacksonV2.Module.forceArtifacts(project, cfg, rs)
                JacksonV2.Junior.forceArtifacts(project, cfg, rs)
                Jackson.DataFormat.forceArtifacts(project, cfg, rs)
                Coroutines.forceArtifacts(project, cfg, rs)
                Grpc.forceArtifacts(project, cfg, rs)
                force(
                    Kotlin.bom,
                    // The IntelliJ Platform artifacts request the 3.0.4 line
                    // while the refreshed baseline is on 3.2.4.
                    Caffeine.lib,
                    // `Coroutines.forceArtifacts` covers the modules list but
                    // not the BOM itself; floor artifacts request the
                    // pre-refresh versions of all three.
                    Coroutines.bom,
                    AtomicFu.lib,
                    Protobuf.javaLib,
                    KotlinPoet.lib,
                    Jackson.bom,
                    Reflect.lib,
                    Base.lib,
                    // The published Compiler was built against the previous
                    // `tool-base`, so it requests `code` one version behind
                    // the one pinned here and `failOnVersionConflict()` has
                    // no way to choose. The two are source-identical.
                    //
                    // Only `code` is listed: `module.gradle.kts` already forces
                    // the other tool-base artifacts for project configurations,
                    // and `failOnVersionConflict()` surfaces any gap loudly.
                    ToolBase.code,
                    Logging.lib,
                    Logging.middleware,
                    Dokka.BasePlugin.lib,
                    Validation.runtime,
                    Validation.javaBundle,
                    CoreJvm.client,
                    CoreJvm.server,
                    Time.lib,
                    Time.javaExtensions,
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

private val INTEGRATION_TEST_TIMEOUT_MINUTES = 30L

val publishedModules: Set<Project> = extensions.getByType<SpinePublishing>().projectsToPublish()

val localPublish = tasks.register("localPublish") {
    val pubTasks = publishedModules.map { p ->
        p.tasks["publishToMavenLocal"]
    }
    dependsOn(pubTasks)
}

val integrationTests = tasks.register<RunBuild>("integrationTests") {
    directory = "$rootDir/tests"
    timeout.set(Duration.ofMinutes(INTEGRATION_TEST_TIMEOUT_MINUTES))
    dependsOn(localPublish)
    subprojects.forEach {
        it.tasks.findByName("test")?.let { testTask ->
            this@register.dependsOn(testTask)
        }
    }
    doLast {
        val f = file("$directory/_out/error-out.txt")
        project.logger.error(f.readText())
    }
}

tasks.named("check") {
    dependsOn(integrationTests)
}
