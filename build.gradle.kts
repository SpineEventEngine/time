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
import io.spine.gradle.internal.DependencyResolution
import io.spine.gradle.internal.Deps
import io.spine.gradle.internal.PublishingRepos
import io.spine.gradle.internal.spinePublishing

buildscript {
    apply(from = "$rootDir/version.gradle.kts")

    @Suppress("RemoveRedundantQualifierName") // Cannot use imports here.
    val resolution = io.spine.gradle.internal.DependencyResolution
    resolution.defaultRepositories(repositories)

    val spineBaseVersion: String by extra
    dependencies {
        classpath("io.spine.tools:spine-model-compiler:$spineBaseVersion")
    }

    resolution.forceConfiguration(configurations)
}

plugins {
    `java-library`
    jacoco
    idea
    `project-report`
    @Suppress("RemoveRedundantQualifierName") // Cannot use imports here.
    io.spine.gradle.internal.Deps.build.apply {
        id("com.google.protobuf") version protobuf.gradlePluginVersion
        id("net.ltgt.errorprone") version errorProne.gradlePluginVersion
    }
}

apply(from = "$rootDir/version.gradle.kts")

extra.apply {
    this["groupId"] = "io.spine"
}

spinePublishing {
    projectsToPublish.addAll(
        "time",
        "testutil-time"
    )
    targetRepositories.addAll(
        PublishingRepos.cloudRepo
        // PublishingRepos.gitHub("LibraryName")
    )
}

allprojects {
    apply(from = "$rootDir/version.gradle.kts")

    group = "io.spine"
    version = extra["versionToPublish"]!!

    apply(from = "$rootDir/config/gradle/dependencies.gradle")
}

subprojects {
    apply {
        plugin("java-library")
        plugin("net.ltgt.errorprone")
        plugin("pmd")
        plugin("checkstyle")
        plugin("com.google.protobuf")
        plugin("maven-publish")
        plugin("idea")
        from(Deps.scripts.projectLicenseReport(project))
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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

    DependencyResolution.defaultRepositories(repositories)

    val spineBaseVersion: String by extra
    dependencies {
        Deps.build.apply {
            errorprone(errorProne.core)
            errorproneJavac(errorProne.javacPlugin)
            implementation("io.spine:spine-base:$spineBaseVersion")
        }
        testImplementation("io.spine:spine-testlib:$spineBaseVersion")
        testImplementation("io.spine.tools:spine-mute-logging:$spineBaseVersion")
        runtimeOnly(Deps.runtime.flogger.systemBackend)
    }

    DependencyResolution.forceConfiguration(configurations)

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
            artifact = Deps.build.protobuf.compiler
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
        with(Deps.scripts) {
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

    apply(from = Deps.scripts.filterInternalJavadocs(project))

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
        with(Deps.scripts) {
            from(pmd(project))
            from(checkstyle(project))
        }
    }
}

apply {
    with(Deps.scripts) {
        // Aggregated coverage report across all subprojects.
        from(jacoco(project))
        // Generate a repository-wide report of 3rd-party dependencies and their licenses.
        from(repoLicenseReport(project))
        // Generate a `pom.xml` file containing first-level dependency of all projects
        // in the repository.
        from(generatePom(project))
    }
}
