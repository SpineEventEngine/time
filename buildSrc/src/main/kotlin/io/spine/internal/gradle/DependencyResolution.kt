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

package io.spine.internal.gradle

import io.spine.internal.dependency.Kotlin
import io.spine.internal.dependency.Okio
import io.spine.internal.dependency.Plexus
import io.spine.internal.dependency.Protobuf
import io.spine.internal.dependency.Truth
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.ResolutionStrategy
import org.gradle.api.artifacts.dsl.RepositoryHandler

/**
 * The function to be used in `buildscript` when a fully-qualified call must be made.
 */
@Suppress("unused")
fun doForceVersions(configurations: ConfigurationContainer, libs: LibrariesForLibs) {
    configurations.forceVersions(libs)
}

/**
 * Forces dependencies used in the project.
 */
fun NamedDomainObjectContainer<Configuration>.forceVersions(libs: LibrariesForLibs) {
    all {
        resolutionStrategy {
            failOnVersionConflict()
            cacheChangingModulesFor(0, "seconds")
            forceProductionDependencies(libs)
            forceTestDependencies(libs)
            forceTransitiveDependencies(libs)
        }
    }
}

private fun ResolutionStrategy.forceProductionDependencies(libs: LibrariesForLibs) {
    @Suppress("DEPRECATION") // Force SLF4J version.
    force(
        libs.animalSniffer,
        libs.googleAuto.common,
        libs.googleAuto.service.annotations,
        libs.checkerFramework.annotations,
        libs.errorProne.annotations,
        libs.errorProne.typeAnnotations,
        libs.errorProne.core,
        libs.findBugs.annotations,
        libs.flogger,
        libs.flogger.runtime.systemBackend,
        libs.guava,
        Kotlin.reflect,
        Kotlin.stdLib,
        Kotlin.stdLibCommon,
        Kotlin.stdLibJdk8,
        Protobuf.libs,
        Protobuf.GradlePlugin.lib,
        io.spine.internal.dependency.Slf4J.lib
    )
}

private fun ResolutionStrategy.forceTestDependencies(libs: LibrariesForLibs) {
    force(
        libs.guava.testLib,
        libs.jUnit.api,
        libs.jUnit.apiGuardian,
        libs.jUnit.params,
        libs.jUnit.legacy,
        libs.jUnit.platformCommons,
        libs.jUnit.platformLauncher,
        Truth.libs
    )
}

/**
 * Forces transitive dependencies of 3rd party components that we don't use directly.
 */
private fun ResolutionStrategy.forceTransitiveDependencies(libs: LibrariesForLibs) {
    force(
        libs.googleAuto.value.annotations,
        libs.gson,
        libs.j2ObjC.annotations,
        Plexus.utils,
        Okio.lib,
        libs.apacheCommons.cli,
        libs.apacheCommons.logging,
        libs.checkerFramework.compatQual,
    )
}

fun NamedDomainObjectContainer<Configuration>.excludeProtobufLite() {

    fun excludeProtoLite(configurationName: String) {
        named(configurationName).get().exclude(
            mapOf(
                "group" to "com.google.protobuf",
                "module" to "protobuf-lite"
            )
        )
    }

    excludeProtoLite("runtimeOnly")
    excludeProtoLite("testRuntimeOnly")
}

@Suppress("unused")
object DependencyResolution {
    @Deprecated(
        "Please use `configurations.forceVersions()`.",
        ReplaceWith("configurations.forceVersions()")
    )
    fun forceConfiguration(configurations: ConfigurationContainer, libs: LibrariesForLibs) {
        configurations.forceVersions(libs)
    }

    @Deprecated(
        "Please use `configurations.excludeProtobufLite()`.",
        ReplaceWith("configurations.excludeProtobufLite()")
    )
    fun excludeProtobufLite(configurations: ConfigurationContainer) {
        configurations.excludeProtobufLite()
    }

    @Deprecated(
        "Please use `applyStandard(repositories)` instead.",
        replaceWith = ReplaceWith("applyStandard(repositories)")
    )
    fun defaultRepositories(repositories: RepositoryHandler) {
        repositories.applyStandard()
    }
}
