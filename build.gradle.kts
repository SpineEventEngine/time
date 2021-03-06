/*
 * Copyright 2021, TeamDev. All rights reserved.
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

import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import io.spine.internal.dependency.ErrorProne
import io.spine.internal.dependency.Flogger
import io.spine.internal.dependency.JUnit
import io.spine.internal.dependency.Protobuf
import io.spine.internal.gradle.PublishingRepos
import io.spine.internal.gradle.Scripts
import io.spine.internal.gradle.applyStandard
import io.spine.internal.gradle.forceVersions
import io.spine.internal.gradle.spinePublishing
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


buildscript {
    apply(from = "$rootDir/version.gradle.kts")

    io.spine.internal.gradle.doApplyStandard(repositories)
    io.spine.internal.gradle.doForceVersions(configurations)

    val spineBaseVersion: String by extra
    dependencies {
        classpath("io.spine.tools:spine-mc-java:$spineBaseVersion")
    }
}

plugins {
    `java-library`
    // For newer Kotlin version please visit [https://kotlinlang.org/docs/eap.html#build-details].
    kotlin("jvm") version io.spine.internal.dependency.Kotlin.version
    jacoco
    idea
    `project-report`

    @Suppress("RemoveRedundantQualifierName") // Cannot use imports here.
    io.spine.internal.dependency.Protobuf.GradlePlugin.apply {
        id(id).version(version)
    }
    @Suppress("RemoveRedundantQualifierName") // Cannot use imports here.
    io.spine.internal.dependency.ErrorProne.GradlePlugin.apply {
        id(id).version(version)
    }
}

apply(from = "$rootDir/version.gradle.kts")
spinePublishing {
    targetRepositories.addAll(setOf(
        PublishingRepos.cloudRepo,
        PublishingRepos.gitHub("time")
    ))
    projectsToPublish.addAll(
        "time",
        "testutil-time"
    )
}

allprojects {
    apply(from = "$rootDir/version.gradle.kts")

    group = "io.spine"
    version = extra["versionToPublish"]!!
}

subprojects {

    apply {
        plugin("java-library")
        plugin("kotlin")
        plugin("com.google.protobuf")
        plugin("net.ltgt.errorprone")
        plugin("pmd")
        plugin("checkstyle")
        plugin("maven-publish")
        plugin("idea")
        from(Scripts.projectLicenseReport(project))
    }

    val javaVersion = JavaVersion.VERSION_1_8
    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    kotlin {
        explicitApi()
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = javaVersion.toString()
            useIR = true
        }
    }

    // Specific setup for a Travis build,
    // which prevents appearing of warning messages in build logs.
    //
    // It is expected that warnings are viewed and analyzed in the local build logs.
    val isTravis = System.getenv("TRAVIS") == "true"
    if (isTravis) {
        // Set the maximum number of Javadoc warnings to print.
        //
        // If the parameter value is zero, all warnings will be printed.
        tasks.javadoc {
            val opt = options
            if (opt is CoreJavadocOptions) {
                opt.addStringOption("Xmaxwarns", "1")
            }
        }
    }

    repositories.applyStandard()

    val spineBaseVersion: String by extra
    dependencies {
        errorprone(ErrorProne.core)
        errorproneJavac(ErrorProne.javacPlugin)
        api(kotlin("stdlib-jdk8"))

        testImplementation("io.spine.tools:spine-testlib:$spineBaseVersion")
        testImplementation(JUnit.runner)
        runtimeOnly(Flogger.Runtime.systemBackend)
    }

    configurations.forceVersions()

    val sourcesRootDir = "$projectDir/src"
    val generatedRootDir = "$projectDir/generated"
    sourceSets {
        main {
            proto.srcDir("$sourcesRootDir/main/proto")
            java.srcDirs(
                "$generatedRootDir/main/java",
                "$generatedRootDir/main/spine",
                "$sourcesRootDir/main/java"
            )
            resources.srcDirs(
                "$generatedRootDir/main/resources",
                "$sourcesRootDir/main/resources"
            )
        }
        test {
            proto.srcDir("$sourcesRootDir/test/proto")
            java.srcDirs(
                "$generatedRootDir/test/java",
                "$generatedRootDir/test/spine",
                "$sourcesRootDir/test/java"
            )
            resources.srcDirs(
                "$generatedRootDir/test/resources",
                "$sourcesRootDir/test/resources"
            )
        }
    }

    tasks.test {
        useJUnitPlatform {
            includeEngines("junit-jupiter")
        }
    }

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

    apply {
        with(Scripts) {
            from(testOutput(project))
            from(javadocOptions(project))
            from(javacArgs(project))
        }
    }

    tasks.register("sourceJar", Jar::class) {
        from(sourceSets.main.get().allJava)
        archiveClassifier.set("sources")
    }

    tasks.register("testOutputJar", Jar::class) {
        from(sourceSets.test.get().output)
        archiveClassifier.set("test")
    }

    tasks.register("javadocJar", Jar::class) {
        from("$projectDir/build/docs/javadoc")
        archiveClassifier.set("javadoc")

        dependsOn("javadoc")
    }

    apply(from = Scripts.filterInternalJavadocs(project))

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

    apply {
        with(Scripts) {
            from(pmd(project))
            from(checkstyle(project))
        }
    }
}

apply {
    with(Scripts) {
        from(jacoco(project))
        from(repoLicenseReport(project))
        from(generatePom(project))
    }
}
