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

import io.spine.internal.dependency.AutoService
import io.spine.internal.dependency.Spine
import io.spine.internal.gradle.excludeProtobufLite
import io.spine.internal.gradle.protobuf.suppressDeprecationsInKotlin
import io.spine.internal.gradle.publish.IncrementGuard
import io.spine.protodata.gradle.plugin.LaunchProtoData
import io.spine.tools.mc.gradle.modelCompiler
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileTool

plugins {
    protobuf
    `detekt-code-analysis`
    id(protoData.pluginId)
    id(mcJava.pluginId)
}

apply<IncrementGuard>()

dependencies {
    annotationProcessor(AutoService.processor)
    compileOnly(AutoService.annotations)

    val spine = Spine(project)
    protoData(spine.validation.java)
    api(spine.base)
    implementation(spine.validation.runtime)

    testImplementation(project(":testutil-time"))
}

configurations {
    excludeProtobufLite()
}

val generatedDir:String by extra("$projectDir/generated")

protobuf {
    generatedFilesBaseDir = generatedDir
}

/**
 * Suppress the "legacy" validation from McJava in favour of tha based on ProtoData.
 */
modelCompiler.java.codegen.validation().skipValidation()

protoData {
    renderers(
        "io.spine.validation.java.PrintValidationInsertionPoints",
        "io.spine.validation.java.JavaValidationRenderer",

        // Suppress warnings in the generated code.
        "io.spine.protodata.codegen.java.file.PrintBeforePrimaryDeclaration",
        "io.spine.protodata.codegen.java.suppress.SuppressRenderer"
    )
    plugins(
        "io.spine.validation.ValidationPlugin",
    )
}

val generatedSourceProto = "$buildDir/generated/source/proto"

/**
 * Manually suppress deprecations in the generated Kotlin code until ProtoData does it.
 *
 * Also, remove the generated vanilla proto code.
 */
tasks.withType<LaunchProtoData>().forEach { task ->
    task.doLast {
        sourceSets.forEach { sourceSet ->
            suppressDeprecationsInKotlin(generatedDir, sourceSet.name)
        }

        delete("$buildDir/generated-proto")
        delete(generatedSourceProto)
    }
}

val ensureInterimKotlinErased by tasks.registering {
    doLast {
        delete(generatedSourceProto)
    }
}

val compileKotlin: KotlinCompile<*> by tasks.getting(KotlinCompile::class) {
    val generatedSourceProtoDir = File(generatedSourceProto)
    val notInSourceDir: (File) -> Boolean = { file -> !file.residesIn(generatedSourceProtoDir) }
    val thisTask = this as KotlinCompileTool

    val filteredKotlin = thisTask.sources.filter(notInSourceDir).toSet()
    with(thisTask.sources as ConfigurableFileCollection) {
        setFrom(filteredKotlin)
    }

    dependsOn(ensureInterimKotlinErased)
}

fun File.residesIn(directory: File): Boolean =
    canonicalFile.startsWith(directory.absolutePath)
