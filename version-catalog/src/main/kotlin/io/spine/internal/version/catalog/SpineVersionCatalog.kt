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
 * A Gradle plugin for [Settings] which registers a Version Catalog with
 * dependencies used within Spine-related projects.
 *
 * Please take a look on [Version Catalog](https://docs.gradle.org/current/userguide/platforms.html).
 */
@Suppress("UnstableApiUsage", "unused")
class SpineVersionCatalog : Plugin<Settings> {

    companion object {
        private const val ENTRIES_PKG = "io.spine.internal.dependency"
    }

    /**
     * Applies this plugin to the given [Settings].
     *
     * In particular, this method does the following:
     *
     *  1. Creates a new `libs` catalog.
     *  2. Finds all declared [entries][VersionCatalogEntry].
     *  3. Add them all to the created catalog.
     */
    override fun apply(settings: Settings) {
        val catalog = settings.createCatalog("libs")
        val entries = findDeclaredEntries(ENTRIES_PKG)
        entries.forEach { it.addTo(catalog) }
    }

    /**
     * Creates a new catalog with the given name in this [Settings].
     */
    private fun Settings.createCatalog(name: String): VersionCatalogBuilder {
        val result = dependencyResolutionManagement.versionCatalogs.create(name)
        return result
    }

    /**
     * Finds all declared entries within the given package.
     *
     * The method utilizes reflection.
     *
     * @param pkg a package to scan.
     */
    private fun findDeclaredEntries(pkg: String): Set<VersionCatalogEntry> {
        val builder = ConfigurationBuilder().forPackage(pkg)
        val reflections = Reflections(builder)
        val entries = reflections.getSubTypesOf(VersionCatalogEntry::class.java)
            .map { it.kotlin }
            .mapNotNull { it.objectInstance }
            .onEach { it.resolveNestedObjects() }
            .toSet()
        return entries
    }

    /**
     * Triggers initializing of the nested objects.
     *
     * It forces the code they contain to execute. And, in particular, the code
     * which declares the delegated properties using [VersionCatalogEntry].
     */
    private fun VersionCatalogEntry.resolveNestedObjects() =
        this::class.nestedClasses.forEach { it.objectInstance }
}
