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
 * A pseudonym, by which an item is known in a version catalog.
 *
 * Each item within the catalog has its unique alias.
 *
 * Aliases perform two functions:
 *
 *  1. Navigation. By the alias, one can locate and access an item in the catalog.
 *  2. Referencing. One item in a version catalog can use another item, and this
 *     linkage is done by aliases.
 *
 * Please, consider an example of how the aliases are mapped to the generated
 * type-safe accessors of a version catalog:
 *
 * ```
 * "kotlinx-coroutines" => libs.kotlin.coroutines
 * "kotlinx-coroutines-gradlePlugin" => libs.kotlin.coroutines.gradlePlugin
 * "kotlinx-coroutines-runtime-jvm" => libs.kotlin.runtime.jvm
 * "kotlinx-coroutines-runtime-clr" => libs.kotlin.runtime.clr
 * ```
 */
internal typealias Alias = String

/**
 * A record represents a single item in a version catalog.
 *
 * It is an atomic and indivisible unit, which can be written to the catalog.
 */
internal interface CatalogRecord {

    /**
     * A pseudonym, by which this record is known in the catalog.
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
 * Represents a library, version of which is specified by the given
 * version reference.
 */
internal data class LibraryRecord(
    override val alias: Alias,
    val module: String,
    val versionRef: Alias
) : CatalogRecord {

    override fun writeTo(catalog: VersionCatalogBuilder) {
        val group = module.substringBefore(':')
        val artifact = module.substringAfter(':')
        catalog.run { library(alias, group, artifact).versionRef(versionRef) }
    }
}

/**
 * Represents a Gradle plugin, version of which is specified by the given
 * version reference.
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
 * Please note, it is implied, that the given set consists of aliases,
 * each of which denotes a library.
 */
internal data class BundleRecord(
    override val alias: Alias,
    val libs: Set<Alias>
) : CatalogRecord {

    override fun writeTo(catalog: VersionCatalogBuilder) {
        catalog.bundle(alias, libs.toList())
    }
}
