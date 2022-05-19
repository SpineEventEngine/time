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

plugins {
    java
    groovy
    `kotlin-dsl`
    pmd
}

repositories {
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
}

configurations.all {
    resolutionStrategy {
        failOnVersionConflict()
        force(
            libs.guava,
            libs.jackson.databind,
            libs.httpClient.google,
            libs.slf4J.api,
            libs.jackson.core,
            libs.kotlin.stdLibJdk8,
            libs.kotlin.stdLib,
            libs.kotlin.stdLibCommon,
            libs.apacheHttp.core,
            libs.jackson.dataformatXml,
            libs.kotlin.reflect,
            libs.jackson.moduleKotlin,
            "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.1",
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1",
        )
    }
}

dependencies {

    // A line below makes the generated `LibrariesForLibs` extension class
    // available in `main` source set.

    // It does not mean our dependencies are available in `main` sources.
    // It means we can fetch them in a type-safe manner from a `Project`
    // instance, in which this extension is registered.

    // ==> `val libs = project.extensions.getByType<LibrariesForLibs>()`

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.errorProne.gradlePlugin)
    implementation(libs.googleCloud.artifactRegistry.authCommon)
    implementation(libs.grGit.core)
    implementation(libs.guava)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.dataformatXml)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.licenseReport.gradlePlugin)
    implementation(libs.protobuf.gradlePlugin)

    // These guys below use a fat jar with Kotlin runtime inside. This is a
    // reason for two warnings. I'm not sure if we can just exclude those jars.
    // But it eliminates warnings.

    implementation(libs.dokka.gradlePlugin)
    implementation(libs.dokka.basePlugin) {
        exclude("org.jetbrains.dokka", "kotlin-analysis-compiler")
        exclude("org.jetbrains.dokka", "kotlin-analysis-intellij")
    }
}
