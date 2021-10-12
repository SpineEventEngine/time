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

package io.spine.internal.gradle.report.coverage

import io.spine.internal.gradle.applyPlugin
import io.spine.internal.gradle.children
import io.spine.internal.gradle.findTask
import io.spine.internal.gradle.sourceSets
import java.io.File
import java.util.*
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.SourceSetOutput
import org.gradle.kotlin.dsl.get
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Configures JaCoCo plugin to produce `jacocoRootReport` task which accumulates
 * the test coverage results from all projects in a multi-project Gradle build.
 */
class JacocoConfig(
    private val rootProject: Project,
    private val reportsDir: File,
    private val projects: Iterable<Project>
) {

    companion object {
        fun applyTo(project: Project) {

            project.applyPlugin(BasePlugin::class.java)

            val javaProjects: Iterable<Project> =
                if (project.subprojects.isNotEmpty()) {
                    //todo: think of applying the plugin right here, but for `java` projects only.
                    project.children.applyPlugin(JacocoPlugin::class.java)
                    project.subprojects
                } else {
                    project.applyPlugin(JacocoPlugin::class.java)
                    listOf(project)
                }
            val reportsDir = project.rootDir.resolve("/subreports/jacoco/")

            JacocoConfig(project.rootProject, reportsDir, javaProjects)
        }
    }

    private fun configure() {

        // ---- `CopyReports` task.
        val tasks = rootProject.tasks

        val everyExecData = mutableListOf<ConfigurableFileCollection>()
        projects.forEach { project ->
            val jacocoTestReport = project.findTask<JacocoReport>("jacocoTestReport")
            val executionData = jacocoTestReport.executionData
            everyExecData.add(executionData)
        }

        val originalLocation = rootProject.files(everyExecData);

        val copyReports = tasks.register("copyReports", Copy::class.java) {
            from(originalLocation)
            into(reportsDir)
            rename {
                "${UUID.randomUUID()}.exec"
            }
        }

        // ----  End of `CopyReports` task.

        val allSourceSets = Projects(projects).sourceSets()
        val mainJavaSrcDirs = allSourceSets.mainJavaSrcDirs(rootProject)
        val humanProducedSources: FileCollection = mainJavaSrcDirs.producedByHuman()
        val filter =
            CodebaseFilter(rootProject, mainJavaSrcDirs, allSourceSets.mainOutputs())

        val humanProducedCompiledFiles = filter.humanProducedCompiledFiles()

        val rootReport = tasks.register("jacocoRootReport", JacocoReport::class.java) {
            dependsOn(copyReports)

            additionalSourceDirs.from(humanProducedSources)
            sourceDirectories.from(humanProducedSources)
            executionData.from(rootProject.fileTree(reportsDir))

            classDirectories.from(humanProducedCompiledFiles)
            additionalClassDirs.from(humanProducedCompiledFiles)

            reports {
                html.required.set(true)
                xml.required.set(true)
                csv.required.set(false)
            }
            onlyIf { true }
        }

        rootProject
            .findTask<Task>("check")
            .dependsOn(rootReport)
    }
}

internal class Projects(
    private val projects: Iterable<Project>
) {

    fun sourceSets(): SourceSets {
        val sets = projects.asSequence().map { it.sourceSets }.toList()
        return SourceSets(sets)
    }
}

internal class SourceSets(
    private val sourceSets: Iterable<SourceSetContainer>
) {

    fun mainJavaSrcDirs(project: Project): FileCollection {
        val files = sourceSets.asSequence().map { it["main"].allJava.srcDirs }.flatMap { it }
        return project.files(files)
    }

    fun mainOutputs(): Set<SourceSetOutput> {
        return sourceSets.asSequence().map { it["main"].output }.toSet()
    }
}
