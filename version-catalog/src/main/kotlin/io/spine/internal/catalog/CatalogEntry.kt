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

import io.spine.internal.Actions
import org.gradle.api.initialization.dsl.VersionCatalogBuilder

internal open class CatalogEntry : CatalogEntryDsl, CatalogContributor {

    private val builderActions = Actions<VersionCatalogBuilder>()
    override val alias: Alias = alias()

    open fun initialize() {
        // No action.
    }

    override fun accept(catalog: VersionCatalogBuilder) = builderActions.play(catalog)

    protected fun builder(action: VersionCatalogBuilder.() -> Unit) = builderActions.add(action)

    protected open fun resolve(relative: String): Alias =
        when (relative) {
            "" -> Alias(alias.parent)
            alias.relative -> alias
            else -> alias + relative
        }

    private fun alias(): Alias {
        val clazz = this::class.java
        val clazzName = clazz.camelName()
        val outer = clazz.enclosingClass?.kotlin?.objectInstance
        val result = if (outer is CatalogEntry) outer.alias + clazzName else Alias(clazzName)
        return result
    }

    private fun Class<*>.camelName() = simpleName.replaceFirstChar { it.lowercase() }
}
