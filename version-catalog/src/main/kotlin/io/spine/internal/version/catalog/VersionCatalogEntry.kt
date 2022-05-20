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
import org.gradle.api.initialization.dsl.VersionCatalogBuilder

internal open class VersionCatalogEntry : VersionCatalogEntryDsl {

    private val builderActions = Actions<VersionCatalogBuilder>()
    private val baseAlias = baseAlias()

    override val version: String? = null
    override val module: String? = null
    override val bundle: Set<LibraryAlias>? = null
    override val id: String? = null

    override fun addTo(catalog: VersionCatalogBuilder) {
        builderActions.play(catalog)
    }

    override fun version(relativeAlias: String, value: String): VersionAlias {
        val alias = resolveAlias(relativeAlias)
        build { version(alias, value) }
        return VersionAlias(alias)
    }

    override fun module(group: String, artifact: String) = provideDelegate { property ->
        val alias = module(property.name, group, artifact)
        delegate(alias)
    }

    override fun module(relativeAlias: String, group: String, artifact: String): LibraryAlias {
        val alias = resolveAlias(relativeAlias)
        build { library(alias, group, artifact).version(version!!) }
        return LibraryAlias(alias)
    }

    override fun bundle(relativeAlias: String, libs: Set<LibraryAlias>): BundleAlias {
        val alias = resolveAlias(relativeAlias)
        build { bundle(alias, libs.map { it.value }) }
        return BundleAlias(alias)
    }

    override fun plugin(relativeAlias: String, id: String, version: String): PluginAlias {
        val alias = resolveAlias(relativeAlias)
        build { plugin(alias, id).version(version) }
        return PluginAlias(alias)
    }

    fun initialize() {

        version?.let {
            version(baseAlias, it)
        }
        module?.let {
            val group = it.substringBefore(':')
            val artifact = it.substringAfter(':')
            module(baseAlias, group, artifact)
        }
        bundle?.let {
            bundle(baseAlias, it)
        }
        id?.let {
            plugin(baseAlias, it, version!!)
        }

        resolveNestedObjects()
    }

    private fun resolveNestedObjects() = this::class.nestedClasses.forEach { it.objectInstance }

    private fun build(action: VersionCatalogBuilder.() -> Unit) = builderActions.add(action)

    /**
     * Calculates a base alias for this entry.
     *
     * Usually, it is a class name with respect to its nesting.
     */
    private fun baseAlias(): String {
        val clazz = this::class.java
        val outer = clazz.enclosingClass?.kotlin?.objectInstance
        return if (outer is VersionCatalogEntry) "${outer.baseAlias}-${clazz.camelName()}"
        else clazz.camelName()
    }

    private fun Class<*>.camelName() = simpleName.replaceFirstChar { it.lowercase() }

    /**
     * Returns an absolute alias for the given relative one.
     *
     * The absolute alias is calculated in respect to [baseAlias].
     */
    private fun resolveAlias(relativeAlias: String): String {
        val result = if (baseAlias.endsWith(relativeAlias)) baseAlias
        else "$baseAlias-$relativeAlias"
        return result
    }
}
