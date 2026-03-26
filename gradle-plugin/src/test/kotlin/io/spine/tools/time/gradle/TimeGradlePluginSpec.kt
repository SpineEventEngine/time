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
import io.spine.tools.meta.MavenArtifact
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("`TimeGradlePlugin` should")
internal class TimeGradlePluginSpec {

    @Nested
    inner class `When 'useJavaExtensions' is` {

        @Test
        fun `true, adds implementation dependency on 'spine-time-java'`() {
            val project = buildProject { useJavaExtensions.set(true) }
            project.hasImplDep(TimeLibrary.javaExtensions).shouldBeTrue()
        }

        @Test
        fun `false by default, adds no implementation dependency on 'spine-time-java'`() {
            val project = buildProject { }
            project.hasImplDep(TimeLibrary.javaExtensions).shouldBeFalse()
        }
    }

    @Nested
    inner class `When 'useKotlinExtensions' is` {

        @Test
        fun `true, adds implementation dependency on 'spine-time-kotlin'`() {
            val project = buildProject { useKotlinExtensions.set(true) }
            project.hasImplDep(TimeLibrary.kotlinExtensions).shouldBeTrue()
        }

        @Test
        fun `false by default, adds no implementation dependency on 'spine-time-kotlin'`() {
            val project = buildProject { }
            project.hasImplDep(TimeLibrary.kotlinExtensions).shouldBeFalse()
        }
    }

    @Nested
    inner class `When 'useTestLib' is` {

        @Test
        fun `true, adds testImplementation dependency on 'spine-time-testlib'`() {
            val project = buildProject { useTestLib.set(true) }
            project.hasTestImplDep(TimeLibrary.testLib).shouldBeTrue()
        }

        @Test
        fun `false by default, adds no testImplementation dependency on 'spine-time-testlib'`() {
            val project = buildProject { }
            project.hasTestImplDep(TimeLibrary.testLib).shouldBeFalse()
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
    project.extensions.getByType(TimeGradleExtension::class.java).configure()
    @Suppress("UnstableApiUsage")
    (project as ProjectInternal).evaluate()
    return project
}

private fun Project.hasImplDep(artifact: MavenArtifact): Boolean =
    configurations.getByName("implementation").dependencies.any {
        it.group == artifact.group && it.name == artifact.name
    }

private fun Project.hasTestImplDep(artifact: MavenArtifact): Boolean =
    configurations.getByName("testImplementation").dependencies.any {
        it.group == artifact.group && it.name == artifact.name
    }
