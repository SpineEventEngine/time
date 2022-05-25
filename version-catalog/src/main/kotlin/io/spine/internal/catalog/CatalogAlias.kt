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

/**
 * Alias is like a path, by which an item (version, library, etc.) can be
 * referenced in a generated type-safe accessor of a version catalog.
 *
 * Consider the following examples of mapping aliases to accessors:
 *
 * ```
 * "kotlinx-coroutines" => libs.kotlin.coroutines
 * "kotlinx-coroutines-gradlePlugin" => libs.kotlin.coroutines.gradlePlugin
 * "kotlinx-coroutines-runtime-jvm" => libs.kotlin.runtime.jvm
 * "kotlinx-coroutines-runtime-clr" => libs.kotlin.runtime.clr
 * ```
 *
 * Inheritors of this class bring only a semantic value with no
 * additional functionality. They are intended to help clearly distinguish
 * on an entry of which type the given alias references.
 *
 * [Mapping aliases to accessors](https://docs.gradle.org/current/userguide/platforms.html#sub:mapping-aliases-to-accessors).
 */
internal open class CatalogAlias(val absolute: String) {

    companion object {
        private const val SEPARATOR = '-'
    }

    val parent by lazy { absolute.substringBeforeLast(SEPARATOR) }
    val relative by lazy { absolute.substringAfterLast(SEPARATOR) }

    operator fun plus(relative: String) = CatalogAlias("$absolute-$relative")

    override fun toString() = absolute
}

internal class VersionAlias(value: String): CatalogAlias(value)
internal fun CatalogAlias.toVersion() = VersionAlias(absolute)

internal class LibraryAlias(value: String): CatalogAlias(value)
internal fun CatalogAlias.toLibrary() = LibraryAlias(absolute)

internal class BundleAlias(value: String): CatalogAlias(value)
internal fun CatalogAlias.toBundle() = BundleAlias(absolute)

internal class PluginAlias(value: String): CatalogAlias(value)
internal fun CatalogAlias.toPlugin() = PluginAlias(absolute)
