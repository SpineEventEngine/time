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

package io.spine.internal.catalog

import java.io.File
import java.nio.file.Files
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.assertDoesNotThrow

/**
 * Verifies the generated type-safe accessors for `Dummy` dependency.
 *
 * `Dummy` dependency is an imaginary library, which exists for two reasons:
 *
 *  1. Showcasing API for dependency declarations.
 *  2. Functional testing in conditions, which are very close to real life.
 *
 * Actually, our `SpineVersionCatalog` provides code upon Gradle-provided
 * `VersionCatalogBuilder`. This code can fill up the passed catalog. And, as
 * for now, there's no any other legitimate way (except for a true functional test)
 * to check if this code executes successfully on a real instance of `VersionCatalogBuilder`.
 *
 * See issue in Gradle: https://github.com/gradle/gradle/issues/20807
 *
 * A note about `FunctionName` suppression. In JUnit in Kotlin, it is okay
 * to write test names just in function name. But IDEA does not report it as en
 * error only for `test` source set. It's unclear how to disable it for custom
 * source sets.
 */
@Suppress("FunctionName") // See docs above.
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("`SpineVersionCatalog` should")
class SpineVersionCatalogFunctionalTest {

    private lateinit var projectDir: File

    @BeforeAll
    fun setUpProject() {
        projectDir = Files.createTempDirectory(javaClass.simpleName).toFile()
        createSettingsFile()
        createBuildFile()
    }

    @AfterAll
    fun cleanUp() {
        projectDir.deleteRecursively()
    }

    /**
     * The test prepares an empty Gradle project in a temporary directory
     * and builds it.
     *
     * This project fetched `SpineVersionCatalog` from MavenLocal. Thus, our code
     * should be published to MavenLocal in advance.
     *
     * The simplest way to achieve this is by means of Gradle:
     *
     * ```
     * tasks {
     *     named("functionalTest") {
     *         dependsOn(named("publishToMavenLocal")
     *     }
     * }
     * ```
     *
     * A build file of this dummy project has assertions upon the generated
     * accessors to `Dummy` dependency. When any of assertions fails, the build
     * fails as well, making the test not passed.
     *
     * @see `io.spine.internal.catalog.entries.Dummy`
     * @see `src/functionalTest/resources/build.gradle.kts`
     */
    @Test
    fun `fill up an existing version catalog`() {
        val runner = GradleRunner.create()
            .withArguments("help", "--stacktrace")
            .withProjectDir(projectDir)

        assertDoesNotThrow {
            runner.build()
        }
    }

    private fun createSettingsFile() = copyFromResources("settings.gradle.kts")

    private fun createBuildFile() = copyFromResources("build.gradle.kts")

    private fun copyFromResources(file: String) {
        val content = javaClass.getResource("/$file")!!.readText()
        projectDir.resolve(file).writeText(content)
    }
}
