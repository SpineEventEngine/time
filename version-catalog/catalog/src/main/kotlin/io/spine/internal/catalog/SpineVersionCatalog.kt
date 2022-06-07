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

import io.spine.internal.catalog.entry.CatalogEntry
import org.gradle.api.initialization.dsl.VersionCatalogBuilder
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder

/**
 * This catalog contains dependencies, which are used in Spine-related projects.
 *
 * In order to use this catalog, one should perform the following:
 *
 *  1. Obtain this class on a classpath of settings file.
 *  2. Create a version catalog.
 *  3. Call [useIn] on a newly created catalog.
 *
 * Below is an example of how to obtain this catalog in the project.
 *
 * In `settings.gradle.kts` file of the project:
 *
 * ```
 * buildscript {
 *     repositories {
 *         mavenCentral()
 *     }
 *     dependencies {
 *         classpath("io.spine.internal:spine-version-catalog:2.0.0")
 *     }
 * }
 *
 * dependencyResolutionManagement {
 *     versionCatalogs {
 *         create("libs") {
 *             SpineVersionCatalog.useIn(this)
 *         }
 *     }
 * }
 * ```
 *
 * In order to add a new dependency to this catalog, create an object declaration
 * in [io.spine.internal.catalog.entry] package. Take a look on a special `Dummy`
 * dependency in README file to quickly grasp API of a dependency declaration.
 */
@Suppress("unused")
class SpineVersionCatalog {
    companion object {

        private const val pkg = "io.spine.internal.catalog.entry"

        /**
         * Fills up the given version catalog with dependencies, declared in
         * this catalog.
         */
        fun useIn(catalog: VersionCatalogBuilder) {
            val entries = findEntries()
            val records = entries.flatMap { it.allRecords() }
            records.forEach { it.writeTo(catalog) }
        }

        /**
         * This method utilizes reflection in order to scan the package for
         * declared [catalog entries][CatalogEntry].
         *
         * The [Reflections] builder is configured to search for all subclasses
         * of [CatalogEntry], declared in the [given package][pkg]. The found
         * entries, in order to be included into the resulting set, should meet
         * the following criteria:
         *
         *  1. Be an object declaration. Only objects can serve as concrete entries.
         *  2. Be a top-level declared. Only root entries should be asked for records.
         *     Then, they will ask their nested entries accordingly.
         */
        private fun findEntries(): Set<CatalogEntry> {
            val builder = ConfigurationBuilder().forPackage(pkg)
            val reflections = Reflections(builder)
            val result = reflections.getSubTypesOf(CatalogEntry::class.java)
                .filter { it.enclosingClass == null }
                .mapNotNull { it.kotlin.objectInstance }
                .toSet()
            return result
        }
    }
}
