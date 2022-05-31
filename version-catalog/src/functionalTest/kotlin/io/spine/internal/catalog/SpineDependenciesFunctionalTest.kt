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

import java.nio.file.Files
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

// Actually, our plugin operates only upon a Gradle-provided `VersionCatalogBuilder`.
//
// Thus, before publishing the plugin, it is better to check if the
// resulted code upon the builder can assemble a catalog instance.

// Sometimes happens it can't. Due to aliases mismatch or incorrect alias name.
// The catalog builder doesn't check this on its own.
//
// And as for now, there is no legitimate way to check this out without
// a true functional test.
//
// See issue in Gradle: https://github.com/gradle/gradle/issues/20807

/**
 * Checks out that the code upon `VersionCatalogBuilder`, produced by the plugin,
 * can assemble an instance of the catalog.
 */
@Suppress("FunctionName")
@DisplayName("`SpineDependencies` should")
class SpineDependenciesFunctionalTest {

    private val projectDir = Files.createTempDirectory(this::class.simpleName).toFile()

    /**
     * The test verifies that a build doesn't fail when the plugin is applied.
     *
     * The build fails if a version catalog can't be assembled.
     */
    @Test
    fun `fill up an existing version catalog`() {
        createSettingsFile()
        createBuildFile()

        val runner = GradleRunner.create()
            .withArguments("help")
            .withProjectDir(projectDir)

        assertDoesNotThrow {
            runner.build()
        }
    }

    /**
     * Applies the plugin to a dummy project.
     *
     * The plugin is fetched from MavenLocal.
     *
     * This, it should be published to MavenLocal in advance:
     *
     * ```
     * tasks {
     *     named("functionalTest") {
     *         dependsOn(named("publishToMavenLocal")
     *     }
     * }
     * ```
     */
    private fun createSettingsFile() = copyFromResources("settings.gradle.kts")

    private fun createBuildFile() = copyFromResources("build.gradle.kts")

    private fun copyFromResources(file: String) {
        val content = javaClass.getResource("/$file")!!.readText()
        projectDir.resolve(file).writeText(content)
    }
}
