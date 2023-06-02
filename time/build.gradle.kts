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
import io.spine.internal.dependency.Validation
import io.spine.internal.gradle.publish.IncrementGuard
import org.gradle.jvm.tasks.Jar

plugins {
    protobuf
    `java-module`
    `kotlin-jvm-module`
    id(protoData.pluginId)
    id(mcJava.pluginId)
}

apply<IncrementGuard>()

dependencies {
    annotationProcessor(AutoService.processor)
    compileOnly(AutoService.annotations)

    protoData(Validation.java)
    api(Spine.base)
    implementation(Validation.runtime)

    testImplementation(Spine.testlib)
    testImplementation(project(":testutil-time"))
}

configurations {
    excludeProtobufLite()
}

/**
 * Suppress the "legacy" validation from McJava in favour of tha based on ProtoData.
 */
modelCompiler.java.codegen.validation().skipValidation()

protoData {
    renderers(
        "io.spine.validation.java.PrintValidationInsertionPoints",
        "io.spine.validation.java.JavaValidationRenderer",
    )
    plugins(
        "io.spine.validation.ValidationPlugin",
    )
}

/**
 * Forcibly remove the code by `protoc` under the `build` directory
 * until ProtoData can handle it by itself.
 *
 * The generated code is transferred by ProtoData into `$projectDir/generated` while it
 * processes it. But ProtoData does not handle the Gradle model fully yet. That's why we have the
 * build error because of the duplicated source code files are attempted to be packed
 * into a source code JAR.
 *
 * We should not set the `DuplicatesStrategy.INCLUDE` as we did before, because it will
 * lead to accidental removal of the generated code added by McJava (and ProtoData) into
 * the vanilla Protobuf Java sources.
 *
 * Therefore, the `Jar` tasks are configured to depend on `removeGeneratedVanillaCode`.
 */
val removeGeneratedVanillaCode by tasks.registering(Delete::class) {
    delete("$buildDir/generated/source/proto")
}

tasks {
    withType<Jar>().configureEach {
        dependsOn(removeGeneratedVanillaCode)
    }
}
