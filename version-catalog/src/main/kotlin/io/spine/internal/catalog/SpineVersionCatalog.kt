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
 * The class represents all dependencies, declared within
 * [io.spine.internal.catalog.entries] package.
 *
 * The dependencies, declared there are used in Spine-related projects.
 *
 * This class locates all catalog entries, declared in the package. A catalog
 * entry is quite complex structure itself, which can also have children
 * and parents. Thus, it can't be written into the version catalog directly.
 * Firstly, entries are transformed into catalog records, and then they are
 * written into the given version catalog.
 *
 * In order to add a new dependency to this set, create an object declaration
 * in the package, mentioned above. Take a look on a special `Dummy` dependency
 * to quickly grasp API of a dependency declarations.
 *
 * See: [Dummy][io.spine.internal.catalog.entries.Dummy]
 */
@Suppress("unused")
class SpineVersionCatalog {
    companion object {

        private const val pkg = "io.spine.internal.catalog.entries"

        /**
         * Fills up the given version catalog with dependencies, which are used
         * in Spine-related projects.
         */
        fun useIn(catalog: VersionCatalogBuilder) {
            val entries = findEntries()
            val records = entries.flatMap { it.allRecords() }
            records.forEach { it.writeTo(catalog) }
        }

        /**
         * This method utilizes reflection in order to scan the package for
         * declared [catalog entries][CatalogEntry].
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
