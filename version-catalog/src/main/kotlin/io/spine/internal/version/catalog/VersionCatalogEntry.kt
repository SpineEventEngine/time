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

import java.util.*
import kotlin.properties.PropertyDelegateProvider
import org.gradle.api.initialization.dsl.VersionCatalogBuilder

internal open class VersionCatalogEntry {

    private val actions = mutableListOf<VersionCatalogBuilder.() -> Unit>()

    private var baseAlias = baseAlias()

    private fun baseAlias(): String {

        val clazz = this::class.java
        var name = clazz.simpleName.replaceFirstChar { it.lowercase() }

        if(clazz.isNested()) {
            val nestedName = clazz.enclosingClass.simpleName.replaceFirstChar { it.lowercase() }
            name = "$nestedName-$name"
        }

        return name
    }

    private fun builder(action: VersionCatalogBuilder.() -> Unit) {
        actions.add(action)
    }

    private val RelativeAlias.absolute: AbsoluteAlias
        get() = "$baseAlias-$this"

    fun lib(relativeAlias: RelativeAlias, gav: String): LibraryReference {
        val absoluteAlias = relativeAlias.absolute
        builder { library(absoluteAlias, gav) }
        return LibraryReference(absoluteAlias)
    }

    fun lib(gav: String): LibraryReference {
        builder { library(baseAlias, gav) }
        return LibraryReference(baseAlias)
    }

    fun gav(value: String) = PropertyDelegateProvider<Any?, ReferenceDelegate<LibraryReference>> { _, property ->
        val ref = if(property.name == "lib") lib(value) else lib(property.name, value)
        ReferenceDelegate(ref)
    }

    fun addTo(catalog: VersionCatalogBuilder) {
        actions.forEach { it(catalog) }
    }
}

private fun Class<*>.isNested() = Objects.nonNull(enclosingClass)

