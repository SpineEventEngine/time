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

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin that configures the Spine Time library in a project.
 *
 * When applied, the plugin:
 *  1. Adds `io.spine:spine-time` as an `implementation` dependency.
 *  2. Adds `io.spine:spine-time-java` if [TimeGradleExtension.useJavaExtensions] is `true`.
 *  3. Adds `io.spine:spine-time-kotlin` if [TimeGradleExtension.useKotlinExtensions] is `true`.
 *  4. Adds `io.spine:spine-time-testlib` as a `testImplementation` dependency
 *     if [TimeGradleExtension.useTestLib] is `true`.
 *
 * **Prerequisite:** a JVM language plugin (e.g. `java`, `java-library`, or `kotlin("jvm")`)
 * must be applied to the target project before this plugin, so that the `implementation`
 * and `testImplementation` configurations exist at the time the plugin runs.
 */
public class TimeGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(
            TimeGradleExtension.NAME,
            TimeGradleExtension::class.java,
            project
        )
        project.addDependency(TimeLibrary.runtime.coordinates)
        project.afterEvaluate {
            if (extension.useJavaExtensions.get()) {
                project.addDependency(TimeLibrary.javaExtensions.coordinates)
            }
            if (extension.useKotlinExtensions.get()) {
                project.addDependency(TimeLibrary.kotlinExtensions.coordinates)
            }
            if (extension.useTestLib.get()) {
                project.addDependency(TimeLibrary.testLib.coordinates, "testImplementation")
            }
        }
    }
}

private fun Project.addDependency(coordinates: String, configuration: String = "implementation") {
    val config = configurations.findByName(configuration)
        ?: error(
            "Configuration '$configuration' not found in project '$path'. " + when (configuration) {
                "testImplementation" ->
                    "Ensure a test source set is present, or set `useTestLib = false`."
                else ->
                    "Apply a JVM language plugin before 'io.spine.time'."
            }
        )
    config.dependencies.add(dependencies.create(coordinates))
}
