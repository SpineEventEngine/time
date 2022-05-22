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

package io.spine.internal.catalog.entry

import org.gradle.api.Action
import org.gradle.api.artifacts.MutableVersionConstraint
import org.gradle.api.initialization.dsl.VersionCatalogBuilder
import org.gradle.api.provider.Property

@Suppress("CAST_NEVER_SUCCEEDS")
class LoggingVersionCatalogBuilder : VersionCatalogBuilder {

    override fun getName(): String {
        println("getName()")
        throw IllegalStateException()
    }

    override fun getDescription(): Property<String> {
        println("getDescription()")
        throw IllegalStateException()
    }

    override fun from(dependencyNotation: Any) {
        println("from($dependencyNotation)")
        throw IllegalStateException()
    }

    override fun version(alias: String, versionSpec: Action<in MutableVersionConstraint>): String {
        println("version($alias, $versionSpec)")
        return alias
    }

    override fun version(alias: String, version: String): String {
        println("version($alias, $version)")
        return alias
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun alias(alias: String): VersionCatalogBuilder.AliasBuilder {
        println("alias($alias)")
        throw IllegalStateException()
    }

    override fun library(
        alias: String,
        group: String,
        artifact: String
    ): VersionCatalogBuilder.LibraryAliasBuilder {
        println("library($alias, $group, $artifact)")
        return LibraryAliasBuilder
    }

    override fun library(alias: String, groupArtifactVersion: String) {
        println("library($alias, $groupArtifactVersion)")
    }

    override fun plugin(alias: String, id: String): VersionCatalogBuilder.PluginAliasBuilder {
        println("plugin($alias, $id)")
        return PluginAliasBuilder
    }

    override fun bundle(alias: String, aliases: MutableList<String>) {
        println("bundle($alias, $aliases)")
    }

    override fun getLibrariesExtensionName(): String {
        println("getLibrariesExtensionName()")
        throw IllegalStateException()
    }
}

object LibraryAliasBuilder : VersionCatalogBuilder.LibraryAliasBuilder {

    override fun version(versionSpec: Action<in MutableVersionConstraint>) = Unit

    override fun version(version: String) = Unit

    override fun versionRef(versionRef: String) = Unit

    override fun withoutVersion() = Unit

}

object PluginAliasBuilder : VersionCatalogBuilder.PluginAliasBuilder {

    override fun version(versionSpec: Action<in MutableVersionConstraint>) = Unit

    override fun version(version: String) = Unit

    override fun versionRef(versionRef: String) = Unit
}
