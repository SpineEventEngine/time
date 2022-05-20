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

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.VersionCatalogBuilder
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder

/**
 * Set of pre-declared dependencies that are used in Spine-related projects.
 *
 * Contains:
 *
 *  1. Versions.
 *  2. Libraries.
 *  3. Bundles of libraries.
 *  4. Plugins.
 */
open class SpineDependencies {

    /**
     * Registers Spine dependencies in the given version catalog.
     */
    @Suppress("unused")
    fun useIn(catalog: VersionCatalogBuilder) {
        val oldEntries = findDeclaredEntriesOld()
        oldEntries.forEach { it.addTo(catalog) }

        val locator = VersionCatalogEntriesLocator.forPackage("io.spine.internal.dependency")
        val newEntries = locator.find()
        newEntries.forEach { it.addTo(catalog) }
    }

    /**
     * Finds all declared [entries][VersionCatalogEntryOld] within
     * `io.spine.internal.dependency` package.
     *
     * This method utilizes reflection.
     */
    private fun findDeclaredEntriesOld(): Set<VersionCatalogEntryOld> {
        val entriesLocation = "io.spine.internal.dependency"
        val builder = ConfigurationBuilder().forPackage(entriesLocation)
        val reflections = Reflections(builder)
        val entries = reflections.getSubTypesOf(VersionCatalogEntryOld::class.java)
            .map { it.kotlin }
            .mapNotNull { it.objectInstance }
            .onEach { it.resolveNestedObjects() }
            .toSet()
        return entries
    }

    /**
     * Triggers initializing of the nested objects in this [VersionCatalogEntryOld].
     *
     * It forces the code they contain to execute. We use nested objects for
     * scopes demarcation.
     *
     * Please see docs to [VersionCatalogEntryOld] for details.
     */
    private fun VersionCatalogEntryOld.resolveNestedObjects() =
        this::class.nestedClasses.forEach { it.objectInstance }
}

/**
 * A Gradle plugin for [Settings], which registers [SpineDependencies] extension.
 */
@Suppress("unused")
class SpineVersionCatalog : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.extensions.create("spineDependencies", SpineDependencies::class.java)
    }
}
