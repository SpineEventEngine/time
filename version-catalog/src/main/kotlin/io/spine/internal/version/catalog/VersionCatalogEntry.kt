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

import io.spine.internal.Actions
import java.util.*
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import org.gradle.api.initialization.dsl.VersionCatalogBuilder

internal open class VersionCatalogEntry {

    private val builderActions = Actions<VersionCatalogBuilder>()
    private val baseAlias = baseAlias()

    fun addTo(builder: VersionCatalogBuilder) {
        builderActions.play(builder)
    }

    fun lib(gav: String) = PropertyDelegateProvider<Any?, LibraryReference> { _, property ->
        val alias = alias(property.name)
        builderActions.add { library(alias, gav) }
        val reference = LibraryReference(alias)
        reference
    }

    fun lib(relativeAlias: String, gav: String): LibraryReference {
        val alias = alias(relativeAlias)
        builderActions.add { library(alias, gav) }
        val reference = LibraryReference(alias)
        return reference
    }

    fun bundle(vararg libs: LibraryReference) = PropertyDelegateProvider<Any?, BundleReference> { _, property ->
        val alias = alias(property.name)
        val aliases = libs.map { it.alias }
        builderActions.add { bundle(alias, aliases) }
        val reference = BundleReference(alias)
        reference
    }

    fun version(value: String) = PropertyDelegateProvider<Any?, VersionReference> { _, property ->
        val alias = alias(property.name)
        builderActions.add { version(alias, value) }
        val reference = VersionReference(alias)
        reference
    }

    fun plugin(id: String, version: String) = PropertyDelegateProvider<Any?, PluginReference> { _, property ->
        val alias = alias(property.name)
        builderActions.add { plugin(alias, id).version(version) }
        val reference = PluginReference(alias)
        reference
    }

    private fun baseAlias(): String {

        val clazz = this::class.java
        var name = clazz.simpleName.replaceFirstChar { it.lowercase() }

        if (Objects.nonNull(clazz.enclosingClass)) {
            val nested = clazz.enclosingClass
            val nestedName = nested.simpleName.replaceFirstChar { it.lowercase() }
            name = "$nestedName-$name"
        }

        return name
    }

    private fun alias(relativeAlias: String): String {
        val result = if (baseAlias.endsWith(relativeAlias)) baseAlias
                     else "$baseAlias-$relativeAlias"
        return result
    }
}

sealed class VersionCatalogItemReference<T : VersionCatalogItemReference<T>>(val alias: String)
    : ReadOnlyProperty<Any?, T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = this as T
}

class LibraryReference(alias: String) : VersionCatalogItemReference<LibraryReference>(alias)
class BundleReference(alias: String) : VersionCatalogItemReference<BundleReference>(alias)
class VersionReference(alias: String) : VersionCatalogItemReference<VersionReference>(alias)
class PluginReference(alias: String) : VersionCatalogItemReference<PluginReference>(alias)
