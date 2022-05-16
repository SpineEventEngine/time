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

package io.spine.internal.version.catalog

import org.gradle.internal.impldep.org.junit.Rule
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("`SpineVersionCatalog` should")
class SpineVersionCatalogTest {

    val tempFolder = TemporaryFolder()

    private fun projectDir() = tempFolder.root
    private fun buildFile() = projectDir().resolve("build.gradle")
    private fun settingsFile() = projectDir().resolve("settings.gradle")

    @Test
    @DisplayName("register a new version catalog when applied")
    fun apply() {
        println("I'm running......")
        val process = Runtime.getRuntime().exec("./gradlew publishToMavenLocal")
        process.waitFor()

        tempFolder.create()

        buildFile().writeText("")
        settingsFile().writeText("""
        buildscript {
            repositories {
                mavenLocal()
                mavenCentral()
            }
            dependencies {
                classpath("io.spine.internal:spine-version-catalog:+")
            }
        }
        apply {
            plugin("io.spine.internal.version-catalog")
        }
        """.trimIndent())

        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("help")
        runner.withProjectDir(projectDir())

        val result = runner.build()
        assert(result.output.contains("Welcome to Gradle"))
    }
}
