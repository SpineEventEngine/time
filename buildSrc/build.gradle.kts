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
    mavenLocal()
    gradlePluginPortal()
    mavenCentral()
}

configurations.all {
    resolutionStrategy {

        /*
         Failing on each conflict leads to a bit bigger `force()` block,
         but eliminates warnings. Also, builds are more reproducible when dynamic
         version resolution is suppressed.
         */

        failOnVersionConflict()

        force(
            libs.apacheHttp.core,
            libs.guava,
            libs.httpClient.google,
            libs.jackson.core,
            libs.jackson.databind,
            libs.jackson.dataformatXml,
            libs.jackson.moduleKotlin,

            /*
             Suppressing of Kotlin libraries eliminates warnings about different
             Kotlin versions on the classpath.
             */

            libs.kotlin.reflect,
            libs.kotlin.stdLib,
            libs.kotlin.stdLib.common,
            libs.kotlin.stdLib.jdk8,
            libs.kotlinX.coroutines.core,
            libs.kotlinX.coroutines.core.jvm,

            libs.slf4J.api,
        )
    }
}

dependencies {

    /*
     We add the implementation dependency on the class of `libs` extension
     in order to make the generated `LibrariesForLibs` available in `main`
     source set.

     It does not mean our dependencies will be available in `main` sources.
     It means we can fetch them in a type-safe manner from a `Project` instance,
     in which this extension is registered.

     For example:
     val libs = project.extensions.getByType<LibrariesForLibs>()
     */

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

    /*
     Dokka uses a fat jar with Kotlin runtime inside. One more Kotlin version.
     This is a reason for two warnings.

     It's not certain if we can just exclude those jars. Thus, before merge these
     changes into `config`, it should be checked out on a repository, where Dokka
     is used. And if not, a comment should be left here, mentioning this fact and
     describing why it is so.
     */

    implementation(libs.dokka.gradlePlugin)
    implementation(libs.dokka.basePlugin) {
        exclude("org.jetbrains.dokka", "kotlin-analysis-compiler")
        exclude("org.jetbrains.dokka", "kotlin-analysis-intellij")
    }
}
