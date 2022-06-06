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
 * A single, atomic unit, which can be written into a version catalog.
 *
 * Records define what information is necessary to write one or another item
 * into a version catalog. Record's properties strictly reflect the way, an item
 * is stored in the catalog.
 *
 * Similarly to [CatalogNotation]s, records define what information is needed
 * to create one or another catalog-compatible item. Records and notations have
 * similar properties and class hierarchy, but they are different. In contrast
 * to notations, records are citizens of an implementation site. They sas what
 * is needed from the point of view of how this information is actually stored
 * in the catalog.
 *
 * Consider the following example:
 *
 * ```
 * // A user defines a plugin, using the corresponding notation.
 *
 * val plugin = object : PluginNotation {
 *     override val id: String = "kotlin-jvm"
 *     override val module: String = "org.jetbrains:kotlin-jvm"
 *     override val version: String = "1.6.21"
 *     override val alias: Alias = "kotlin.jvm"
 * }
 * ```
 *
 * In order to represent this notation in [VersionCatalogBuilder], three records
 * should be written there: [VersionRecord], [LibraryRecord], [PluginRecord].
 *
 * A record can write itself into the given catalog.
 *
 * This interface, in particular, doesn't describe any concrete item in the catalog.
 * It just serves as a common foundation for other records.
 */
interface CatalogRecord {

    /**
     * A name, by which this record will be known in the catalog.
     *
     * For example: `kotlin.stdLib.common.jvm`.
     */
    val alias: Alias

    /**
     * Writes this record to the given catalog.
     */
    fun writeTo(catalog: VersionCatalogBuilder)
}

/**
 * Represents a version item, which can be directly written into a version catalog.
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
        catalog.version(alias, value)
    }
}

/**
 * Represents a library item, which can be directly written into a version catalog.
 *
 * [module] is a group and artifact of a library, seperated by a colon.
 *
 * For example: `io.spine:spine-core`.
 *
 * Version for the library is obtained by the reference. Thus, the given [versionRef]
 * should point to a [VersionRecord].
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
 * Represents a plugin item, which can be directly written into a version catalog.
 *
 * [id] is a unique name, by which a plugin is represented in both Gradle Plugin Portal
 * and in the project.
 *
 * For example: `org.jetbrains.kotlin.jvm`.
 *
 * Version of the plugin is obtained by the reference. Thus, the given [versionRef]
 * should point to a [VersionRecord].
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
 * Represents a named set of libraries, which can be directly written into
 * a version catalog
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
