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

package io.spine.internal.gradle.report.license

import com.github.jk1.license.LicenseReportExtension
import com.github.jk1.license.ModuleData
import com.github.jk1.license.ProjectData
import com.github.jk1.license.render.ReportRenderer
import io.spine.internal.gradle.report.license.Configuration.runtime
import io.spine.internal.gradle.report.license.Configuration.runtimeClasspath
import io.spine.internal.markup.MarkdownDocument
import java.io.File
import kotlin.reflect.KCallable
import org.gradle.api.Project

/**
 * Renders the dependency report in markdown.
 */
internal class MarkdownReportRenderer(
    private val filename: String
) : ReportRenderer {

    override fun render(data: ProjectData) {
        val project = data.project
        val outputFile = outputFile(project)
        val document = MarkdownDocument()
        val template = Template(project, document)

        template.writeHeader()
        Dependencies.of(data).printTo(document)
        template.writeFooter()

        document.appendToFile(outputFile)
    }

    private fun outputFile(project: Project): File {
        val config =
            project.extensions.findByName("licenseReport") as LicenseReportExtension
        return File(config.outputDir).resolve(filename)
    }
}

private class Dependencies(
    private val runtime: Iterable<ModuleData>,
    private val compileTooling: Iterable<ModuleData>

) {

    companion object {
        fun of(data: ProjectData): Dependencies {
            val runtimeDeps = mutableListOf<ModuleData>()
            val compileToolingDeps = mutableListOf<ModuleData>()
            data.configurations.forEach { config ->
                if (config.isOneOf(runtime, runtimeClasspath)) {
                    runtimeDeps.addAll(config.dependencies)
                } else {
                    compileToolingDeps.addAll(config.dependencies)
                }
            }
            return Dependencies(runtimeDeps.toSortedSet(), compileToolingDeps.toSortedSet())
        }
    }

    fun printTo(out: MarkdownDocument) {
        out.printSection("Runtime", runtime)
            .printSection("Compile, tests and tooling", compileTooling)
    }
}

private fun ModuleData.print(out: MarkdownDocument) {
    out.ol("")

    this.print(ModuleData::getGroup, out, "Group")
        .print(ModuleData::getName, out, "Name")
        .print(ModuleData::getVersion, out, "Version")

    if (this.poms.isEmpty() && this.manifests.isEmpty()) {
        out.bold("No license information found")
        return
    }

    var projectUrlDone = false
    if (this.manifests.isNotEmpty() && this.poms.isNotEmpty()) {
        val manifest = this.manifests.first()
        val pomData = this.poms.first()
        if (manifest.url != null && pomData.projectUrl != null && manifest.url == pomData.projectUrl) {
            out.add("\n     * **Project URL:** [${manifest.url}](${manifest.url})")
            projectUrlDone = true
        }
    }

    if (this.manifests.isNotEmpty()) {
        val manifest = this.manifests.first()
        if (!manifest.url.isNullOrEmpty() && !projectUrlDone) {
            out.add("\n     * **Manifest Project URL:** [${manifest.url}](${manifest.url})")
        }
        if (!manifest.license.isNullOrEmpty()) {
            when {
                manifest.license.startsWith("http") -> {
                    out.add("\n     * **Manifest license URL:** [${manifest.license}](${manifest.license})")
                }
                manifest.hasPackagedLicense -> {
                    out.add("\n     * **Packaged License File:** [${manifest.license}](${manifest.url})")
                }
                else -> {
                    out.add("\n     * **Manifest License:** ${manifest.license} (Not packaged)")
                }
            }
        }
    }

    if (this.poms.isNotEmpty()) {
        val pomData = this.poms.first()
        if (!pomData.projectUrl.isNullOrEmpty() && !projectUrlDone) {
            out.add("\n     * **POM Project URL:** [${pomData.projectUrl}](${pomData.projectUrl})")

        }
        if (pomData.licenses != null) {
            pomData.licenses.forEach { license ->
                out.add("\n     * **POM License: ${license.name}**")

                if (!license.url.isNullOrEmpty()) {
                    when {
                        license.url.startsWith("http") -> {
                            out.add(" - [${license.url}](${license.url})")
                        }
                        else -> {
                            out.add(" **License:** ${license.url}")
                        }
                    }
                }
            }
        }
    }
    out.add("\n")
}

internal class References(
    private val projectUrl: String?,
    private val licenseLinks: Set<Link>
){

    internal companion object {

        fun of(module: ModuleData): References {
            if(module.poms.isEmpty() && module.manifests.isEmpty()) {
                return References(null, setOf())
            }

            val projectUrl: String? = module.projectUrl()
            val licenses: Set<Link> = module.licenses()
            return References(projectUrl, licenses)
        }
    }
}

internal fun ModuleData.projectUrl(): String? {
    val pomUrl = this.poms.firstOrNull()?.projectUrl
    if(!pomUrl.isNullOrBlank()) {
        return pomUrl
    }
    return this.manifests.firstOrNull()?.url
}

internal fun ModuleData.licenses(): Set<Link> {
    val result = mutableSetOf<Link>()

    val manifestLink: Link? = manifests.firstOrNull()?.let { manifest ->
        val license = manifest.license
        if (!license.isNullOrBlank()) {
            if (license.startsWith("http")) {
                Link(license, license)
            } else {
                Link(license, manifest.url)
            }
        }
        null
    }
    manifestLink?.let { result.add(it) }

    val pomLinks = poms.firstOrNull()?.licenses?.map { license ->
        Link(license.name, license.url)
    }
    pomLinks?.let {
        result.addAll(it)
    }
    return result.toSet()
}

data class Link(val text: String, val url: String?)

private fun ModuleData.print(
    getter: KCallable<*>,
    out: MarkdownDocument,
    title: String
): ModuleData {
    val value = getter.call(this)
    if (value != null) {
        out.add(" **${title}:** ${value}")
    }
    return this
}

private fun MarkdownDocument.printSection(
    title: String,
    modules: Iterable<ModuleData>
): MarkdownDocument {
    this.add("\n## $title")
    modules.forEach {
        it.print(this)
    }
    return this
}
