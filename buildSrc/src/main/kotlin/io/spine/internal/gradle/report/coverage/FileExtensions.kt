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

import java.io.File
import org.gradle.api.file.FileCollection

/**
 * This file contains extension methods and properties for `java.io.File`
 * and for related Gradle objects, such as `FileCollection`.
 */

/**
 * Parses the name of a class from the absolute path of this file.
 *
 * Treats the fragment between the [precedingMarker] and [extension] as the value to look for.
 * In case the fragment is located and it contains `/` symbols, they are treated
 * as Java package delimiters and are replaced by `.` symbols before returning the value.
 *
 * If the absolute path of this file has either no [precedingMarker] or no [extension],
 * returns `null`.
 */
internal fun File.parseClassName(
    precedingMarker: PathMarker,
    extension: FileExtension
): String? {
    val index = this.absolutePath.lastIndexOf(precedingMarker.value)
    return if (index > 0) {
        var inFolder = this.absolutePath.substring(index + precedingMarker.length)
        if (inFolder.endsWith(extension.value)) {
            inFolder = inFolder.substring(0, inFolder.length - extension.length)
            inFolder.replace('/', '.')
        } else {
            null
        }
    } else {
        null
    }
}

/**
 * If this file contains the name of a class — according to the passed [classNameParser]s —
 * appends the specified [destination] with it.
 */
internal fun File.appendTo(
    destination: MutableList<String>,
    vararg classNameParser: (file: File) -> String?
) {
    for (parser in classNameParser) {
        val className = parser.invoke(this)
        if (className != null) {
            destination.add(className)
            break
        }
    }
}

/**
 * Attempts to parse the name of Java source file from the absolute path of this file.
 */
internal fun File.asJavaSourceFileName(): String? =
    this.parseClassName(PathMarker.JAVA_SRC_FOLDER, FileExtension.JAVA_SOURCE)

/**
 * Attempts to parse the name of Java compiled file from the absolute path of this file.
 */
internal fun File.asJavaCompiledFileName(): String? {
    var className = this.parseClassName(PathMarker.JAVA_OUTPUT_FOLDER, FileExtension.COMPILED_CLASS)
    if (className != null && className.contains(ClassMarker.ANONYMOUS.value)) {
        className = className.split(ClassMarker.ANONYMOUS.pattern())[0]
    }
    return className
}

/**
 * Attempts to parse the name of gRPC Java source file from the absolute path of this file.
 */
internal fun File.asGrpcSourceFileName(): String? =
    this.parseClassName(PathMarker.GRPC_SRC_FOLDER, FileExtension.JAVA_SOURCE)

/**
 * Attempts to parse the name of Spine-produced Java source file
 * from the absolute path of this file.
 */
internal fun File.asSpineSourceFileName(): String? =
    this.parseClassName(PathMarker.SPINE_JAVA_SRC_FOLDER, FileExtension.JAVA_SOURCE)

/**
 * Excludes the generated files from this file collection, leaving only those which were
 * created by human beings.
 */
internal fun FileCollection.producedByHuman(): FileCollection {
    return this.filter {
        !it.absolutePath.contains(PathMarker.GENERATED.value)
    }
}

/**
 * Filters this file collection so that only generated files are present.
 */
internal fun FileCollection.generatedOnly(): FileCollection {
    return this.filter {
        it.absolutePath.contains(PathMarker.GENERATED.value)
    }
}
