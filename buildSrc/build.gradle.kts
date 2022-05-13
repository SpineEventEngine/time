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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

kotlin {
    val jvmVersion = JavaLanguageVersion.of(11)

    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(jvmVersion)
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = jvmVersion.toString()
        }
    }
}

dependencies {
    api(libs.licenseReport)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.jackson.databind)
    implementation(libs.jackson.dataformatXml)
    implementation(libs.google.artifactRegistry.authCommon)
    implementation(libs.guava)
    implementation(libs.grgit)
    implementation(libs.errorProne.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.protobuf.gradlePlugin)
    implementation(libs.dokka.gradlePlugin)
    implementation(libs.dokka.basePlugin)
}
