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

/**
 * A record represents a single unit, which can be written to a version catalog.
 */
interface CatalogRecord {

    /**
     * A pseudonym, by which this record will be known in the catalog.
     */
    val alias: Alias

    /**
     * Writes this record to the given catalog.
     */
    fun writeTo(catalog: VersionCatalogBuilder)
}

/**
 * Represents a bare version.
 */
internal data class VersionRecord(
    override val alias: Alias,
    val value: String
) : CatalogRecord {

    override fun writeTo(catalog: VersionCatalogBuilder) {
        catalog.version(alias, value)
    }
}

/**
 * Represents a library.
 *
 * Version for the library is obtained by the reference. Thus, the given
 * [versionRef] should point to a [VersionRecord].
 */
internal data class LibraryRecord(
    override val alias: Alias,
    val module: String,
    val versionRef: Alias
) : CatalogRecord {

    override fun writeTo(catalog: VersionCatalogBuilder) {
        val group = module.substringBefore(':')
        val artifact = module.substringAfter(':')
        catalog.library(alias, group, artifact).versionRef(versionRef)
    }
}

/**
 * Represents a Gradle plugin.
 *
 * Version of the plugin is obtained by the reference. Thus, the given
 * [versionRef] should point to a [VersionRecord].
 */
internal data class PluginRecord(
    override val alias: Alias,
    val id: String,
    val versionRef: Alias
) : CatalogRecord {

    override fun writeTo(catalog: VersionCatalogBuilder) {
        catalog.plugin(alias, id).versionRef(versionRef)
    }
}

/**
 * Represents a named set of libraries.
 *
 * It is expected, that each alias in [libs] points to a [LibraryRecord].
 */
internal data class BundleRecord(
    override val alias: Alias,
    val libs: Set<Alias>
) : CatalogRecord {

    override fun writeTo(catalog: VersionCatalogBuilder) {
        catalog.bundle(alias, libs.toList())
    }
}
