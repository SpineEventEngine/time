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

import io.spine.internal.dependency.ErrorProne
import io.spine.internal.dependency.JUnit
import io.spine.internal.dependency.Spine
import io.spine.internal.gradle.checkstyle.CheckStyleConfig
import io.spine.internal.gradle.github.pages.updateGitHubPages
import io.spine.internal.gradle.javac.configureErrorProne
import io.spine.internal.gradle.javac.configureJavac
import io.spine.internal.gradle.javadoc.JavadocConfig
import io.spine.internal.gradle.kotlin.setFreeCompilerArgs
import io.spine.internal.gradle.report.license.LicenseReporter
import io.spine.internal.gradle.standardToSpineSdk
import io.spine.internal.gradle.testing.configureLogging
import io.spine.internal.gradle.testing.registerTestTasks
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.checkstyle
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.idea
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.jacoco
import org.gradle.kotlin.dsl.pmd
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`
    kotlin("jvm")
    jacoco
    checkstyle
    pmd
    id("net.ltgt.errorprone")
    id("pmd-settings")
    id("dokka-for-java")
    `project-report`
    idea
}
LicenseReporter.generateReportIn(project)
JavadocConfig.applyTo(project)
CheckStyleConfig.applyTo(project)

project.run {
    repositories.standardToSpineSdk()

    addDependencies()

    val javaVersion = JavaVersion.VERSION_11
    configureJava(javaVersion)
    configureKotlin(javaVersion)

    configureTests()
    setupJavadoc()

    val generatedDir:String by extra("$projectDir/generated")
    configureIdea(generatedDir)
    configureTaskDependencies()
}

typealias Subproject = Project

fun Subproject.applyPlugins() {
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

    LicenseReporter.generateReportIn(project)
    JavadocConfig.applyTo(project)
    CheckStyleConfig.applyTo(project)
}

fun Subproject.addDependencies() {
    val spine = Spine(project)
    dependencies {
        errorprone(ErrorProne.core)

        testImplementation(spine.testlib)
        testImplementation(JUnit.runner)
    }
}

fun Subproject.configureJava(javaVersion: JavaVersion) {
    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion

        tasks {
            withType<JavaCompile>().configureEach {
                configureJavac()
                configureErrorProne()
            }
            withType<Jar>().configureEach {
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
        }
    }
}

fun Subproject.configureKotlin(javaVersion: JavaVersion) {
    kotlin {
        explicitApi()

        tasks {
            withType<KotlinCompile>().configureEach {
                kotlinOptions.jvmTarget = javaVersion.toString()
                setFreeCompilerArgs()
            }
        }
    }
}

fun Subproject.configureTests() {
    tasks {
        registerTestTasks()
        test {
            useJUnitPlatform()
            configureLogging()
        }
    }
}

fun Subproject.configureIdea(generatedDir: String) {
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
}

fun Subproject.setupJavadoc() {
    updateGitHubPages(Spine.DefaultVersion.javadocTools) {
        allowInternalJavadoc.set(true)
        rootFolder.set(rootDir)
    }
}
