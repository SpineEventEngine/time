/*
 * Copyright 2026, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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

package io.spine.tools.time.gradle

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.spine.tools.gradle.lib.spineExtension
import io.spine.tools.meta.MavenArtifact
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("`TimeGradlePlugin` should")
internal class TimeGradlePluginSpec {

    @Test
    fun `be applied to a project by its ID`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply("io.spine.time")

        project.plugins.hasPlugin("io.spine.time").shouldBeTrue()
        project.plugins.hasPlugin(TimeGradlePlugin::class.java).shouldBeTrue()
    }

    @Nested
    inner class `When 'useJavaExtensions' is` {

        @Test
        fun `true, adds implementation dependency on 'spine-time-java'`() {
            val project = buildProject { useJavaExtensions.set(true) }
            project.hasDependency(TimeLibrary.javaExtensions).shouldBeTrue()
        }

        @Test
        fun `false by default, adds no implementation dependency on 'spine-time-java'`() {
            val project = buildProject { }
            project.hasDependency(TimeLibrary.javaExtensions).shouldBeFalse()
        }
    }

    @Nested
    inner class `When 'useKotlinExtensions' is` {

        @Test
        fun `true, adds implementation dependency on 'spine-time-kotlin'`() {
            val project = buildProject { useKotlinExtensions.set(true) }
            project.hasDependency(TimeLibrary.kotlinExtensions).shouldBeTrue()
        }

        @Test
        fun `false by default, adds no implementation dependency on 'spine-time-kotlin'`() {
            val project = buildProject { }
            project.hasDependency(TimeLibrary.kotlinExtensions).shouldBeFalse()
        }
    }

    @Nested
    inner class `When 'useTestLib' is` {

        private val testImplementation = "testImplementation"

        @Test
        fun `true, adds testImplementation dependency on 'time-testlib'`() {
            val project = buildProject { useTestLib.set(true) }
            project.hasDependency(TimeLibrary.testLib, testImplementation).shouldBeTrue()
        }

        @Test
        fun `false by default, adds no testImplementation dependency on 'time-testlib'`() {
            val project = buildProject { }
            project.hasDependency(TimeLibrary.testLib, testImplementation).shouldBeFalse()
        }
    }
}

/**
 * Creates a synthetic Gradle project with the `java` and [TimeGradlePlugin] plugins applied,
 * then evaluates it so that all `afterEvaluate` callbacks — including optional-dependency
 * resolution — are executed.
 */
private fun buildProject(configure: TimeGradleExtension.() -> Unit): Project {
    val project = ProjectBuilder.builder().build()
    project.plugins.apply("java")
    project.plugins.apply(TimeGradlePlugin::class.java)
    project.spineExtension<TimeGradleExtension>().configure()
    @Suppress("UnstableApiUsage")
    (project as ProjectInternal).evaluate()
    return project
}

private fun Project.hasDependency(
    artifact: MavenArtifact,
    configuration: String = "implementation"
): Boolean =
    configurations.getByName(configuration).dependencies.any {
        it.group == artifact.group && it.name == artifact.name
    }
