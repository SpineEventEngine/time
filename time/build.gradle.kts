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

plugins {
    id(io.spine.internal.dependency.Protobuf.GradlePlugin.id)
    id("io.spine.protodata")
    id("io.spine.mc-java")
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

sourceSets {
    main { java.srcDir("$generatedDir/main/java") }
    main { kotlin.srcDir("$generatedDir/main/kotlin") }
    test { java.srcDir("$generatedDir/test/java") }
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

/**
 * Manually suppress deprecations in the generated Kotlin code until ProtoData does it.
 */
tasks.withType<LaunchProtoData>().forEach { task ->
    task.doLast {
        sourceSets.forEach { sourceSet ->
            suppressDeprecationsInKotlin(generatedDir, sourceSet.name)
        }
    }
}
