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

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import org.gradle.api.initialization.dsl.VersionCatalogBuilder

typealias Alias = String
typealias RelativeAlias = String
typealias AbsoluteAlias = String

interface CatalogReference {
    val value: Alias
}

@JvmInline value class VersionReference(override val value: AbsoluteAlias) : CatalogReference
@JvmInline value class LibraryReference(override val value: AbsoluteAlias) : CatalogReference

internal class SpineVersionCatalogBuilder
private constructor(private val builder: VersionCatalogBuilder, private val baseAlias: String) {

    companion object {
        fun wrap(builder: VersionCatalogBuilder, baseAlias: String) =
            SpineVersionCatalogBuilder(builder, baseAlias)
    }

    private val RelativeAlias.absolute: AbsoluteAlias
        get() = "$baseAlias-$this"

    fun version(relativeAlias: RelativeAlias, version: String): VersionReference {
        val absoluteAlias = builder.version(relativeAlias.absolute, version)
        return VersionReference(absoluteAlias)
    }

    fun version(version: String): VersionReference {
        val absoluteAlias = builder.version(baseAlias, version)
        return VersionReference(absoluteAlias)
    }

    fun library(relativeAlias: RelativeAlias, gav: String): LibraryReference {
        val absoluteAlias = relativeAlias.absolute
        builder.library(absoluteAlias, gav)
        return LibraryReference(absoluteAlias)
    }

    fun library(gav: String): LibraryReference {
        builder.library(baseAlias, gav)
        return LibraryReference(baseAlias)
    }

    fun gav(value: String) = PropertyDelegateProvider<Any?, ReferenceDelegate<LibraryReference>> { _, property ->
            val ref = library(property.name, value)
            ReferenceDelegate(ref)
        }

    fun plugin(relativeAlias: RelativeAlias, id: String, version: String) =
        builder.plugin(relativeAlias.absolute, id).version(version)

    fun plugin(id: String, version: String) = builder.plugin(baseAlias, id).version(version)

    fun bundle(relativeAlias: RelativeAlias, aliases: List<LibraryReference>) =
        builder.bundle(relativeAlias.absolute, aliases.map { it.value })
}

internal class ReferenceDelegate<T : CatalogReference>(private val ref: T)
    : ReadOnlyProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = ref
}


