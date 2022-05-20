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

package io.spine.internal.catalog

import org.gradle.api.initialization.dsl.VersionCatalogBuilder

internal interface SimpleCatalogBuilder {

    companion object {
        fun wrap(catalog: VersionCatalogBuilder): SimpleCatalogBuilder =
            CatalogBuilderImpl.wrap(catalog)
    }

    fun version(alias: String, value: String): VersionAlias

    fun library(alias: String, group: String, artifact: String, version: VersionAlias): LibraryAlias

    fun bundle(alias: String, vararg libs: LibraryAlias): BundleAlias

    fun plugin(alias: String, id: String, version: VersionAlias): PluginAlias
}

private class CatalogBuilderImpl(val catalog: VersionCatalogBuilder) : SimpleCatalogBuilder {

    companion object {
        fun wrap(catalog: VersionCatalogBuilder) = CatalogBuilderImpl(catalog)
    }

    override fun version(alias: String, value: String): VersionAlias {
        catalog.version(alias, value)
        val versionAlias = VersionAlias(alias)
        return versionAlias
    }

    override fun library(
        alias: String,
        group: String,
        artifact: String,
        version: VersionAlias
    ): LibraryAlias {

        catalog.library(alias, group, artifact).versionRef(version.absolute)
        val libraryAlias = LibraryAlias(alias)
        return libraryAlias
    }

    override fun bundle(alias: String, vararg libs: LibraryAlias): BundleAlias {
        catalog.bundle(alias, libs.map { it.absolute })
        val bundleAlias = BundleAlias(alias)
        return bundleAlias
    }

    override fun plugin(alias: String, id: String, version: VersionAlias): PluginAlias {
        catalog.plugin(alias, id).versionRef(version.absolute)
        val pluginAlias = PluginAlias(alias)
        return pluginAlias
    }
}
