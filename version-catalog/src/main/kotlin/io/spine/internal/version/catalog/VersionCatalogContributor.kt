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
import org.gradle.api.initialization.dsl.VersionCatalogBuilder

/**
 * A contributor to [Version Catalog](https://docs.gradle.org/current/userguide/platforms.html).
 */
internal abstract class VersionCatalogContributor {

    private var baseAlias = baseAlias()

    internal fun baseAlias(): String {

        val clazz = this::class.java
        var name = clazz.simpleName.replaceFirstChar { it.lowercase() }

        if(clazz.isNested()) {
            val nestedName = clazz.enclosingClass.simpleName.replaceFirstChar { it.lowercase() }
            name = "$nestedName-$name"
        }

        return name
    }

    /**
     * Contributes new dependencies, versions, plugins or bundles
     * to this version catalog.
     */
    protected abstract fun SpineVersionCatalogBuilder.doContribute()

    fun contribute(catalog: VersionCatalogBuilder) {
        val spineBuilder = SpineVersionCatalogBuilder.wrap(catalog, baseAlias)
        spineBuilder.run { doContribute() }
    }
}

private fun Class<*>.isNested() = nonNull(enclosingClass)