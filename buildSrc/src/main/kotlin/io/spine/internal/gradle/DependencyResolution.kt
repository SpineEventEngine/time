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

private fun ResolutionStrategy.forceProductionDependencies(libs: LibrariesForLibs) = with(libs) {
    @Suppress("DEPRECATION") // Force SLF4J version.
    force(
        animalSniffer,
        checkerFramework.annotations,
        errorProne.annotations,
        errorProne.core,
        errorProne.typeAnnotations,
        findBugs.annotations,
        flogger,
        flogger.runtime.systemBackend,
        googleAuto.common,
        googleAuto.service.annotations,
        guava,
        kotlin.reflect,
        kotlin.stdLib,
        kotlin.stdLib.common,
        kotlin.stdLib.jdk8,
        protobuf.gradlePlugin,

        /*
         Previously, we could force bundles, which we represented as iterable
         of strings. But we are not allowed to force version catalog's bundles:

         `bundles.protobuf`

         It's not a bug. It's rather a design desicion, as the error clearly states:
         => Only dependency accessors are supported but not plugin, bundle or version accessors for 'force()'.

         So, we have to open out the given bundle, and force three libraries independently.
         */

        protobuf.java,
        protobuf.javaUtil,
        protobuf.kotlin,

        slf4J,
    )
}

private fun ResolutionStrategy.forceTestDependencies(libs: LibrariesForLibs) = with(libs) {
    force(
        guava.testLib,
        jUnit.api,
        jUnit.apiGuardian,
        jUnit.legacy,
        jUnit.params,
        jUnit.platform.commons,
        jUnit.platform.launcher,
        truth.java8Extension,
        truth.protoExtension,
    )
}

/**
 * Forces transitive dependencies of 3rd party components that we don't use directly.
 */
private fun ResolutionStrategy.forceTransitiveDependencies(libs: LibrariesForLibs) = with(libs) {
    force(
        apacheCommons.cli,
        apacheCommons.logging,
        checkerFramework.compatQual,
        googleAuto.value.annotations,
        gson,
        j2ObjC.annotations,
        okio,
        plexus.utils,
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
