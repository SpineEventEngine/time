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

package io.spine.internal.catalog.model

import org.gradle.api.initialization.dsl.VersionCatalogBuilder

/**
 * An atomic unit, which can be written into a version catalog.
 *
 * Records strictly reflect the way, the information is stored in the catalog.
 * Due to this fact, they don't need any preparations to be written there.
 *
 * This interface, doesn't represent a concrete catalog item. It serves as a common
 * foundation for other records.
 *
 * All concrete records are internal by design. They are assembled by [CatalogEntry]
 * under the hood and not meant to be exposed to end-users. All a user might need
 * to do with a record is to write it into the catalog, which can be done with
 * this public interface.
 */
interface CatalogRecord {

    /**
     * A name, by which this record will be known in the catalog.
     *
     * For example: `kotlin.stdLib.common.jvm`.
     *
     * See documentation to [Alias] to see how a type-safe accessor is generated
     * from an alias.
     */
    val alias: Alias

    /**
     * Writes this record to the given catalog.
     */
    fun writeTo(catalog: VersionCatalogBuilder)
}

/**
 * A version, which can be directly written into a version catalog.
 *
 * [value] is a string representation of a version.
 *
 * For example: `2.0.0-SNAPSHOT.21`.
 */
internal data class VersionRecord(
    override val alias: Alias,
    val value: String
) : CatalogRecord {

    override fun writeTo(catalog: VersionCatalogBuilder) {
        catalog.version(alias.value, value)
    }
}

/**
 * A library, which can be directly written into a version catalog.
 *
 * [module] is a group and artifact of a library, seperated by a colon.
 *
 * For example: `io.spine:spine-core`.
 *
 * A version of the library is referenced by the given [version record][version].
 */
internal data class LibraryRecord(
    override val alias: Alias,
    val module: String,
    val version: VersionRecord
) : CatalogRecord {

    override fun writeTo(catalog: VersionCatalogBuilder) {
        val group = module.substringBefore(':')
        val artifact = module.substringAfter(':')
        catalog.library(alias.value, group, artifact).versionRef(version.alias.value)
    }
}

/**
 * A plugin, which can be directly written into a version catalog.
 *
 * [id] is a unique name, by which a plugin is represented in both Gradle Plugin Portal
 * and in the project.
 *
 * For example: `org.jetbrains.kotlin.jvm`.
 *
 * A version of the plugin is referenced by the given [version record][version].
 */
internal data class PluginRecord(
    override val alias: Alias,
    val id: String,
    val version: VersionRecord
) : CatalogRecord {

    override fun writeTo(catalog: VersionCatalogBuilder) {
        catalog.plugin(alias.value, id).versionRef(version.alias.value)
    }
}

/**
 * A named set of libraries, which can be directly written into a version catalog
 */
internal data class BundleRecord(
    override val alias: Alias,
    val libs: Set<LibraryRecord>
) : CatalogRecord {

    override fun writeTo(catalog: VersionCatalogBuilder) {
        catalog.bundle(alias.value, libs.map { it.alias.value })
    }
}
