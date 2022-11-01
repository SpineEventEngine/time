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

import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import io.spine.internal.dependency.Dokka
import io.spine.internal.dependency.ErrorProne
import io.spine.internal.dependency.JUnit
import io.spine.internal.dependency.Jackson
import io.spine.internal.dependency.Spine
import io.spine.internal.gradle.applyGitHubPackages
import io.spine.internal.gradle.applyStandard
import io.spine.internal.gradle.checkstyle.CheckStyleConfig
import io.spine.internal.gradle.forceVersions
import io.spine.internal.gradle.github.pages.updateGitHubPages
import io.spine.internal.gradle.javac.configureErrorProne
import io.spine.internal.gradle.javac.configureJavac
import io.spine.internal.gradle.javadoc.JavadocConfig
import io.spine.internal.gradle.kotlin.setFreeCompilerArgs
import io.spine.internal.gradle.publish.PublishingRepos
import io.spine.internal.gradle.publish.spinePublishing
import io.spine.internal.gradle.report.coverage.JacocoConfig
import io.spine.internal.gradle.report.license.LicenseReporter
import io.spine.internal.gradle.report.pom.PomGenerator
import io.spine.internal.gradle.test.configureLogging
import io.spine.internal.gradle.test.registerTestTasks
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    apply(from = "$rootDir/version.gradle.kts")

    io.spine.internal.gradle.doApplyStandard(repositories)
    io.spine.internal.gradle.doForceVersions(configurations)

    dependencies {
        classpath(io.spine.internal.dependency.Spine.McJava.pluginLib)
    }

    val spine = io.spine.internal.dependency.Spine(project)
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
    applyStandard()
}

plugins {
    `java-library`
    kotlin("jvm")
    jacoco
    idea
    `project-report`

    id(protobufPlugin)
    id(errorPronePlugin)
    `detekt-code-analysis`
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
        enabled = true
    }
}

// Temporarily use this version, since 3.21.x is known to provide
// a broken `protoc-gen-js` artifact and Kotlin code without access modifiers.
// See https://github.com/protocolbuffers/protobuf-javascript/issues/127.
//     https://github.com/protocolbuffers/protobuf/issues/10593
val protocArtifact = "com.google.protobuf:protoc:3.19.6"

allprojects {
    apply(from = "$rootDir/version.gradle.kts")

    group = "io.spine"
    version = extra["versionToPublish"]!!

    val spine = Spine(project)
    configurations {
        forceVersions()
        all {
            exclude("io.spine:spine-validate")
            resolutionStrategy {
                force(
                    spine.base,
                    spine.validation.runtime,
                    Dokka.BasePlugin.lib,
                    Jackson.databind,
                    protocArtifact
                )
            }
        }
    }
}

subprojects {

    apply {
        plugin("java-library")
        plugin("kotlin")
        plugin("com.google.protobuf")
        plugin("net.ltgt.errorprone")
        plugin("pmd")
        plugin("checkstyle")
        plugin("idea")
        plugin("pmd-settings")
        plugin("jacoco")
        plugin("dokka-for-java")
    }

    repositories {
        applyGitHubPackages("base", project)
        applyStandard()
    }

    val spine = Spine(project)
    dependencies {
        errorprone(ErrorProne.core)
        api(kotlin("stdlib-jdk8"))

        testImplementation(spine.testlib)
        testImplementation(JUnit.runner)
    }

    val javaVersion = JavaVersion.VERSION_11

    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion

        tasks {
            withType<JavaCompile>().configureEach {
                configureJavac()
                configureErrorProne()
            }
            withType<org.gradle.jvm.tasks.Jar>().configureEach {
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
        }
    }

    kotlin {
        explicitApi()

        tasks {
            withType<KotlinCompile>().configureEach {
                kotlinOptions.jvmTarget = javaVersion.toString()
                setFreeCompilerArgs()
            }
        }
    }

    LicenseReporter.generateReportIn(project)
    JavadocConfig.applyTo(project)
    CheckStyleConfig.applyTo(project)

    tasks {
        registerTestTasks()
        test {
            useJUnitPlatform {
                includeEngines("junit-jupiter")
            }
            configureLogging()
        }
    }

    val generatedDir:String by extra("$projectDir/generated")

    protobuf {
        protoc {
            // Temporarily use this version, since 3.21.x is known to provide
            // a broken `protoc-gen-js` artifact.
            // See https://github.com/protocolbuffers/protobuf-javascript/issues/127.
            //
            // Once it is addressed, this artifact should be `Protobuf.compiler`.
            artifact = protocArtifact
        }
        generateProtoTasks {
            all().forEach { task ->
                task.builtins {
                    id("js") {
                        option("library=spine-time-${project.version}")
                    }
                }
            }
        }
    }

    updateGitHubPages(Spine.DefaultVersion.javadocTools) {
        allowInternalJavadoc.set(true)
        rootFolder.set(rootDir)
    }

    // Apply the same IDEA module configuration for each of subprojects.
    idea {
        module {
            with(generatedSourceDirs) {
                add(file("$generatedDir/main/js"))
                add(file("$generatedDir/main/java"))
                add(file("$generatedDir/main/kotlin"))
                add(file("$generatedDir/main/spine"))
            }
            testSources.from(
                file("$generatedDir/test/java"),
                file("$generatedDir/test/kotlin")
            )
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }

    project.configureTaskDependencies()
}

LicenseReporter.mergeAllReports(project)
JacocoConfig.applyTo(project)
PomGenerator.applyTo(project)
