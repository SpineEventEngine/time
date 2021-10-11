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

import io.spine.internal.gradle.report.coverage.CodebaseFilter.Companion.ANONYMOUS_CLASS_MARKER
import io.spine.internal.gradle.report.coverage.CodebaseFilter.Companion.ANONYMOUS_CLASS_PATTERN
import io.spine.internal.gradle.report.coverage.CodebaseFilter.Companion.COMPILED_CLASS_FILE_EXTENSION
import io.spine.internal.gradle.report.coverage.CodebaseFilter.Companion.GENERATED_PATH_MARKER
import io.spine.internal.gradle.report.coverage.CodebaseFilter.Companion.GRPC_SRC_FOLDER_MARKER
import io.spine.internal.gradle.report.coverage.CodebaseFilter.Companion.JAVA_OUTPUT_FOLDER_MARKER
import io.spine.internal.gradle.report.coverage.CodebaseFilter.Companion.JAVA_SOURCE_FILE_EXTENSION
import io.spine.internal.gradle.report.coverage.CodebaseFilter.Companion.JAVA_SRC_FOLDER_MARKER
import io.spine.internal.gradle.report.coverage.CodebaseFilter.Companion.SPINE_JAVA_SRC_FOLDER_MARKER
import java.io.File
import kotlin.streams.toList
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceSetOutput

/**
 * Serves to distinguish the {@code .java} and {@code .class} files built from
 * the Protobuf definitions from the human-created production code.
 */
internal class CodebaseFilter(
    private val project: Project,
    private val srcDirs: FileCollection,
    private val outputDirs: Set<SourceSetOutput>
) {

    internal companion object {
        const val GENERATED_PATH_MARKER = "generated"

        const val JAVA_SRC_FOLDER_MARKER = "/java/"
        const val SPINE_JAVA_SRC_FOLDER_MARKER = "main/spine/"
        const val GRPC_SRC_FOLDER_MARKER = "/main/grpc/"

        const val JAVA_OUTPUT_FOLDER_MARKER = "/main/"

        const val JAVA_SOURCE_FILE_EXTENSION = ".java"
        const val COMPILED_CLASS_FILE_EXTENSION = ".class"
        const val ANONYMOUS_CLASS_MARKER = '$'
        const val ANONYMOUS_CLASS_PATTERN = "\\${ANONYMOUS_CLASS_MARKER}"
    }

    private fun generatedClassNames():  List<String> {
        val generatedSourceFiles = srcDirs.generatedOnly()
        val generatedNames = mutableListOf<String>()
        generatedSourceFiles.forEach { folder ->
            if (folder.exists() && folder.isDirectory) {
                folder.walk()
                    .filter { !it.isDirectory }
                    .forEach { file ->
                        file.appendTo(generatedNames,
                            File::javaSourceFileName,
                            File::grpcSourceFileName,
                            File::spineSourceFileName
                        )
                    }
            }
        }

        return generatedNames
    }

    fun humanProducedCompiledFiles(): List<FileTree> {
        log("Source dirs for code coverage calculation:")
        val srcDirs = project.files(this.srcDirs)
        srcDirs.forEach {
            log(" - ${it}")
        }

        val generatedClassNames = generatedClassNames()
        log(generatedClassNames.joinToString(System.lineSeparator()))
        val nonGeneratedClassTree = outputDirs
            .stream()
            .flatMap { it.classesDirs.files.stream() }
            .map { srcFile ->
                log("Filtering out the generated classes for ${srcFile}.")

                val fileTree = project.fileTree(srcFile)
                fileTree.exclude { element ->
                    val className = element.file.javaCompiledFileName()
                    generatedClassNames.contains(className)
                }
                fileTree
            }.toList()
        return nonGeneratedClassTree
    }

    private fun log(message: String) {
        project.logger.debug(message)
    }

}

internal fun FileCollection.producedByHuman(): FileCollection {
    return this.filter {
        !it.absolutePath.contains(GENERATED_PATH_MARKER)
    }
}

internal fun FileCollection.generatedOnly(): FileCollection {
    return this.filter {
        it.absolutePath.contains(GENERATED_PATH_MARKER)
    }
}

internal fun File.parseClassName(sourceFolderMarker: String, extension: String) : String? {
    val index = this.absolutePath.lastIndexOf(sourceFolderMarker)
    return if (index > 0) {
        var inFolder = this.absolutePath.substring(index + sourceFolderMarker.length)
        if (inFolder.endsWith(extension)) {
            inFolder = inFolder.substring(0, inFolder.length - extension.length)
            inFolder.replace('/', '.')
        } else {
            null
        }
    } else {
        null
    }
}

internal fun File.appendTo(
    destination: MutableList<String>,
    vararg pattern: (file: File) -> String?) {
    for (pt in pattern) {
        val className = pt.invoke(this)
         if(className != null) {
            destination.add(className)
            break;
        }
    }
}

private fun File.javaSourceFileName(): String? =
    this.parseClassName(JAVA_SRC_FOLDER_MARKER, JAVA_SOURCE_FILE_EXTENSION)

internal fun File.javaCompiledFileName(): String? {
    var className = this.parseClassName(JAVA_OUTPUT_FOLDER_MARKER, COMPILED_CLASS_FILE_EXTENSION)
    if(className != null && className.contains(ANONYMOUS_CLASS_MARKER)) {
        className = className.split(ANONYMOUS_CLASS_PATTERN)[0]
    }
    return className
}

internal fun File.grpcSourceFileName(): String? =
    this.parseClassName(GRPC_SRC_FOLDER_MARKER, JAVA_SOURCE_FILE_EXTENSION)

internal fun File.spineSourceFileName(): String? =
    this.parseClassName(SPINE_JAVA_SRC_FOLDER_MARKER, JAVA_SOURCE_FILE_EXTENSION)
