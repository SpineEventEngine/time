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
import io.spine.internal.dependency.ErrorProne
import io.spine.internal.dependency.JUnit
import io.spine.internal.dependency.Protobuf
import io.spine.internal.gradle.publish.PublishingRepos
import io.spine.internal.gradle.applyGitHubPackages
import io.spine.internal.gradle.applyStandard
import io.spine.internal.gradle.checkstyle.CheckStyleConfig
import io.spine.internal.gradle.forceVersions
import io.spine.internal.gradle.github.pages.updateGitHubPages
import io.spine.internal.gradle.javac.configureErrorProne
import io.spine.internal.gradle.javac.configureJavac
import io.spine.internal.gradle.javadoc.JavadocConfig
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
    io.spine.internal.gradle.doForceVersions(configurations, libs)

    val mcJavaVersion: String by extra
    dependencies {
        classpath("io.spine.tools:spine-mc-java:$mcJavaVersion")
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

//    id(io.spine.internal.dependency.Protobuf.GradlePlugin.id)
//    id(io.spine.internal.dependency.ErrorProne.GradlePlugin.id)

    id("com.google.protobuf")
    id("net.ltgt.errorprone")
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

allprojects {

    // Due to a bug, we can't apply scripts.
    // See: https://github.com/gradle/gradle/issues/20717

    /** Versions of the Spine libraries that `time` depends on. */
    extra["mcJavaVersion"] = "2.0.0-SNAPSHOT.83"
    extra["spineBaseVersion"] = "2.0.0-SNAPSHOT.91"
    extra["javadocToolsVersion"] = "2.0.0-SNAPSHOT.75"

    /** The version of this library. */
    val versionToPublish by extra("2.0.0-SNAPSHOT.93")

    group = "io.spine"
    version = versionToPublish
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

    val spineBaseVersion: String by extra
    dependencies {
        errorprone(ErrorProne.core)
        api(kotlin("stdlib-jdk8"))

        testImplementation("io.spine.tools:spine-testlib:$spineBaseVersion")
        testImplementation(JUnit.runner)
    }

    /**
     * Force Error Prone dependencies to `2.10.0`, because in `2.11.0` an empty constructor in
     * [com.google.errorprone.bugpatterns.CheckReturnValue] was removed leading to breaking the API.
     */
    configurations {
        forceVersions(rootProject.libs)
        all {
            resolutionStrategy {
                force(
                    "io.spine:spine-base:$spineBaseVersion",
                    "com.google.errorprone:error_prone_core:2.10.0",
                    "com.google.errorprone:error_prone_annotations:2.10.0",
                    "com.google.errorprone:error_prone_annotation:2.10.0",
                    "com.google.errorprone:error_prone_check_api:2.10.0",
                    "com.google.errorprone:error_prone_test_helpers:2.10.0",
                    "com.google.errorprone:error_prone_type_annotations:2.10.0"
                )
            }
        }
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
        }
    }

    kotlin {
        explicitApi()

        tasks {
            withType<KotlinCompile>().configureEach {
                kotlinOptions {
                    jvmTarget = javaVersion.toString()
                }
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

    val generatedRootDir = "$projectDir/generated"

    protobuf {
        generatedFilesBaseDir = generatedRootDir
        protoc {
            artifact = Protobuf.compiler
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

    val javadocToolsVersion: String by extra
    updateGitHubPages(javadocToolsVersion) {
        allowInternalJavadoc.set(true)
        rootFolder.set(rootDir)
    }

    // Apply the same IDEA module configuration for each of sub-projects.
    idea {
        module {
            with(generatedSourceDirs) {
                add(file("$generatedRootDir/main/js"))
                add(file("$generatedRootDir/main/java"))
                add(file("$generatedRootDir/main/spine"))
            }
            testSourceDirs.add(file("$generatedRootDir/test/java"))
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }
}

LicenseReporter.mergeAllReports(project)
JacocoConfig.applyTo(project)
PomGenerator.applyTo(project)
