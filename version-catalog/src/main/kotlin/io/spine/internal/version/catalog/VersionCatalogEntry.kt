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

import java.util.Objects.nonNull
import kotlin.properties.PropertyDelegateProvider
import org.gradle.api.initialization.dsl.VersionCatalogBuilder

internal open class VersionCatalogEntry {

    private val actions = mutableListOf<VersionCatalogBuilder.() -> Unit>()
    private val baseAlias = baseAlias()
    private val RelativeAlias.absolute: AbsoluteAlias
        get() = if (baseAlias.endsWith(this)) baseAlias else "$baseAlias-$this"

    fun addTo(catalog: VersionCatalogBuilder) {
        actions.forEach { it(catalog) }
    }

    fun lib(relativeAlias: RelativeAlias, gav: String): LibraryReference {
        val absoluteAlias = relativeAlias.absolute
        catalog { library(absoluteAlias, gav) }
        return LibraryReference(absoluteAlias)
    }

    fun lib(gav: String) = PropertyDelegateProvider<Any?, LibraryReference> { _, property ->
        val reference = lib(property.name, gav)
        reference
    }

    fun bundle(relativeAlias: RelativeAlias, vararg libs: LibraryReference): BundleReference {
        val absoluteAlias = relativeAlias.absolute
        catalog { bundle(absoluteAlias, libs.map { it.absoluteAlias }) }
        return BundleReference(absoluteAlias)
    }

    fun bundle(vararg libs: LibraryReference) = PropertyDelegateProvider<Any?, BundleReference> { _, property ->
        val reference = bundle(property.name, *libs)
        reference
    }

    fun version(relativeAlias: RelativeAlias, version: String): VersionReference {
        val absoluteAlias = relativeAlias.absolute
        catalog { version(absoluteAlias, version) }
        return VersionReference(absoluteAlias)
    }

    fun version(value: String) = PropertyDelegateProvider<Any?, VersionReference> { _, property ->
        val reference = version(property.name, value)
        reference
    }

    fun plugin(relativeAlias: RelativeAlias, id: String, version: String): PluginReference {
        val absoluteAlias = relativeAlias.absolute
        catalog { plugin(absoluteAlias, id).version(version) }
        return PluginReference(absoluteAlias)
    }

    fun plugin(id: String, version: String) = PropertyDelegateProvider<Any?, PluginReference> { _, property ->
        val reference = plugin(property.name, id, version)
        reference
    }

    private fun catalog(action: VersionCatalogBuilder.() -> Unit) {
        actions.add(action)
    }

    private fun baseAlias(): String {

        val clazz = this::class.java
        var name = clazz.simpleName.replaceFirstChar { it.lowercase() }

        if (nonNull(clazz.enclosingClass)) {
            val nested = clazz.enclosingClass
            val nestedName = nested.simpleName.replaceFirstChar { it.lowercase() }
            name = "$nestedName-$name"
        }

        return name
    }
}
