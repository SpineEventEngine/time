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

val licenseReportVersion = "2.1"

/**
 * The version of the Kotlin Gradle plugin.
 *
 * Please check that this value matches one defined in
 *  [io.spine.internal.dependency.Kotlin.version].
 */
val kotlinVersion = "1.6.21"

/**
 * The version of Protobuf Gradle Plugin.
 *
 * Please keep in sync. with [io.spine.internal.dependency.Protobuf.GradlePlugin.version].
 *
 * @see <a href="https://github.com/google/protobuf-gradle-plugin/releases">
 *     Protobuf Gradle Plugins Releases</a>
 */
val protobufPluginVersion = "0.8.18"

configurations.all {
    resolutionStrategy {
        // Force Kotlin lib versions avoiding using those bundled with Gradle.
        force(
            "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion",
            "org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion",
            "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion",
            "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion",
            "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"
        )
    }
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
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.jackson.databind)
    implementation(libs.jackson.dataformatXml)

    implementation(libs.google.artifactRegistry.authCommon)
    implementation(libs.guava)
    api("com.github.jk1:gradle-license-report:$licenseReportVersion")
    implementation(libs.grgit)
    implementation(libs.errorProne.gradlePlugin)

    // Add explicit dependency to avoid warning on different Kotlin runtime versions.
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

    implementation("gradle.plugin.com.google.protobuf:protobuf-gradle-plugin:$protobufPluginVersion")
    implementation(libs.dokka.gradlePlugin)
    implementation(libs.dokka.basePlugin)
}
