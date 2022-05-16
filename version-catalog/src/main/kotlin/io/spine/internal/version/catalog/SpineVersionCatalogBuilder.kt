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

package io.spine.internal.version.catalog

import org.gradle.api.Action
import org.gradle.api.artifacts.MutableVersionConstraint
import org.gradle.api.initialization.dsl.VersionCatalogBuilder
import org.gradle.api.provider.Property

internal class SpineVersionCatalogBuilder
private constructor(private val builder: VersionCatalogBuilder, private val baseAlias: String) :
    VersionCatalogBuilder {

    companion object {
        fun wrap(builder: VersionCatalogBuilder, baseAlias: String) =
            SpineVersionCatalogBuilder(builder, baseAlias)
    }

    override fun getName(): String = builder.name

    override fun getDescription(): Property<String> = builder.description

    override fun from(dependencyNotation: Any) = builder.from(dependencyNotation)

    override fun version(alias: String, versionSpec: Action<in MutableVersionConstraint>): String =
        builder.version("$baseAlias-$alias", versionSpec)

    override fun version(alias: String, version: String): String =
        builder.version("$baseAlias-$alias", version)

    fun version(version: String): String =
        builder.version(baseAlias, version)

    @Suppress(
        "DEPRECATION", // This method also returns a deprecated `AliasBuilder`.
        "UnstableApiUsage" // `AliasBuilder` is also incubating.
    )
    @Deprecated(
        "Deprecated in `VersionCatalogBuilder`",
        ReplaceWith("Use pure Kotlin to create sub-aliases.")
    )
    override fun alias(alias: String): VersionCatalogBuilder.AliasBuilder =
        throw IllegalStateException("Not supported")

    override fun library(
        alias: String,
        group: String,
        artifact: String
    ): VersionCatalogBuilder.LibraryAliasBuilder =
        builder.library("$baseAlias-$alias", group, artifact)

    override fun library(alias: String, gav: String) =
        builder.library("$baseAlias-$alias", gav)

    fun library(gav: String) =
        builder.library(baseAlias, gav)

    override fun plugin(alias: String, id: String): VersionCatalogBuilder.PluginAliasBuilder =
        builder.plugin("$baseAlias-$alias", id)

    override fun bundle(alias: String, aliases: List<String>) =
        builder.bundle("$baseAlias-$alias", aliases.map { "$baseAlias-$it" })

    override fun getLibrariesExtensionName(): String = builder.librariesExtensionName
}
