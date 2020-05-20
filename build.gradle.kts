/*
 * Copyright 2020, TeamDev. All rights reserved.
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

import com.google.protobuf.gradle.ProtobufConfigurator.JavaGenerateProtoTaskCollection
import com.google.protobuf.gradle.builtins
import groovy.lang.Closure
import io.spine.gradle.internal.DependencyResolution
import io.spine.gradle.internal.Deps

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
    id("com.google.protobuf").version(io.spine.gradle.internal.Deps.versions.protobufPlugin)
    @Suppress("RemoveRedundantQualifierName") // Cannot use imports here.
    id("net.ltgt.errorprone").version(io.spine.gradle.internal.Deps.versions.errorPronePlugin)
}

extra["projectsToPublish"] = listOf(
        "time",
        "testutil-time"
)

allprojects {
    apply(from = "$rootDir/version.gradle.kts")

    group = "io.spine"
    version = extra["versionToPublish"]!!
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
        from("$rootDir/config/gradle/dependencies.gradle")
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

    dependencies {
        errorprone(Deps.build.errorProneCore)
        errorproneJavac(Deps.build.errorProneJavac)

        Deps.build.protobuf.forEach { api(it) }

        implementation(Deps.build.guava)
        implementation(Deps.build.jsr305Annotations)

        Deps.test.junit5Api.forEach { testImplementation(it) }
        testImplementation(Deps.test.guavaTestlib)

        testRuntimeOnly(Deps.test.junit5Runner)
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
        protobuf.generateProtoTasks(object : Closure<Any>(this) {
            private fun doCall(tasks: JavaGenerateProtoTaskCollection) {
                tasks.all().forEach { task ->
                    task.builtins {
                        maybeCreate("js").option("library=spine-time-${project.version}")
                    }
                }
            }
        })
    }

    apply {
        from(Deps.scripts.testOutput(project))
        from(Deps.scripts.javadocOptions(project))
        from(Deps.scripts.javacArgs(project))
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
            generatedSourceDirs.add(file("$generatedRootDir/main/js"))
            generatedSourceDirs.add(file("$generatedRootDir/main/java"))
            generatedSourceDirs.add(file("$generatedRootDir/main/spine"))
            testSourceDirs.add(file("$generatedRootDir/test/java"))
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }

    apply {
        from(Deps.scripts.pmd(project))
        from(Deps.scripts.checkstyle(project))
    }
}
