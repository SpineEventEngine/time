/*
 * Copyright 2020, TeamDev. All rights reserved.
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

import io.spine.gradle.internal.DependencyResolution
import io.spine.gradle.internal.Deps
import io.spine.gradle.internal.IncrementGuard

plugins {
    `java-library`
    id("io.spine.tools.spine-model-compiler")
}

apply {
    from(Deps.scripts.testArtifacts(project))
    from(Deps.scripts.modelCompiler(project))
}
apply<IncrementGuard>()

DependencyResolution.excludeProtobufLite(configurations)

val spineBaseVersion: String by extra
dependencies {
    annotationProcessor(Deps.build.autoService.processor)
    compileOnly(Deps.build.autoService.annotations)

    api(Deps.build.guava)
    api("io.spine:spine-base:$spineBaseVersion")

    testImplementation("io.spine:spine-testlib:$spineBaseVersion")
    testImplementation(project(":testutil-time"))
}

modelCompiler {
    generateValidation = true
}