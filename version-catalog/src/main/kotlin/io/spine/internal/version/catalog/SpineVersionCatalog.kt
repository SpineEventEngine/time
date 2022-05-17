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

@Suppress("UnstableApiUsage", "unused")
class SpineVersionCatalog : Plugin<Settings> {

    companion object {
        private const val DEPENDENCIES_PKG = "io.spine.internal.dependency"
    }

    override fun apply(settings: Settings) {
        val catalog = settings.createCatalog()

        val contributors = findContributors()
        contributors.forEach { it.contribute(catalog) }

        val entries = fetchEntries()
        entries.forEach { it.addTo(catalog) }
    }

    private fun Settings.createCatalog(): VersionCatalogBuilder {
        val result = dependencyResolutionManagement.versionCatalogs.create("libs")
        return result
    }

    private fun findContributors(): Set<VersionCatalogContributor> {
        val builder = ConfigurationBuilder().forPackage(DEPENDENCIES_PKG)
        val reflections = Reflections(builder)
        val contributors = reflections.getSubTypesOf(VersionCatalogContributor::class.java)
            .map { it.kotlin }
            .mapNotNull { it.objectInstance }
            .toSet()
        return contributors
    }

    private fun fetchEntries(): Set<VersionCatalogEntry> {
        val builder = ConfigurationBuilder().forPackage(DEPENDENCIES_PKG)
        val reflections = Reflections(builder)
        val entries = reflections.getSubTypesOf(VersionCatalogEntry::class.java)
            .map { it.kotlin }
            .mapNotNull { it.objectInstance }
            .toSet()
        return entries
    }
}
