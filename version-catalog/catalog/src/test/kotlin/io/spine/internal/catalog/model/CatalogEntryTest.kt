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

package io.spine.internal.catalog.model

import com.google.common.truth.Truth.assertThat
import io.spine.internal.catalog.model.given.BundleEntry
import io.spine.internal.catalog.model.given.CatalogEntryTestEnv.Companion.bundleRecord
import io.spine.internal.catalog.model.given.CatalogEntryTestEnv.Companion.libraryRecord
import io.spine.internal.catalog.model.given.CatalogEntryTestEnv.Companion.pluginRecord
import io.spine.internal.catalog.model.given.CatalogEntryTestEnv.Companion.versionRecord
import io.spine.internal.catalog.model.given.DirectInheritingEntry
import io.spine.internal.catalog.model.given.EmptyRootEntry
import io.spine.internal.catalog.model.given.ErroneousEntry
import io.spine.internal.catalog.model.given.ExtraBundleEntry
import io.spine.internal.catalog.model.given.ExtraLibraryEntry
import io.spine.internal.catalog.model.given.IndirectInheritingEntry
import io.spine.internal.catalog.model.given.LibraryEntry
import io.spine.internal.catalog.model.given.OuterEntry
import io.spine.internal.catalog.model.given.WithPluginEntry
import io.spine.internal.catalog.model.given.PluginEntry
import io.spine.internal.catalog.model.given.RootEntry
import io.spine.internal.catalog.model.given.VersionEntry
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("`CatalogEntry` should")
internal class CatalogEntryTest {

    @Nested
    inner class `when is root` {

        @Nested
        inner class `optionally declare` {

            @Test
            fun `a version`() {
                val entry = VersionEntry
                val records = entry.allRecords()
                assertThat(records).hasSize(1)

                val record = versionRecord(records)
                assertThat(record.alias).isEqualTo("versionEntry")
                assertThat(record.value).isEqualTo("v0.0.1")
            }

            @Test
            fun `a library`() {
                val entry = LibraryEntry
                val records = entry.allRecords()
                assertThat(records).hasSize(2)

                val library = libraryRecord(records)
                assertThat(library.alias).isEqualTo("libraryEntry")
                assertThat(library.module).isEqualTo("com.company:lib")

                val version = versionRecord(records)
                assertThat(library.version).isEqualTo(version)
            }

            @Test
            fun `an extra library`() {
                val entry = ExtraLibraryEntry
                val records = entry.allRecords()
                assertThat(records).hasSize(2)

                val library = libraryRecord(records)
                assertThat(library.alias).isEqualTo("extraLibraryEntry-core")
                assertThat(library.module).isEqualTo("com.company:core-lib")

                val version = versionRecord(records)
                assertThat(library.version).isEqualTo(version)
            }

            @Test
            fun `a plugin`() {
                val entry = PluginEntry
                val records = entry.allRecords()
                assertThat(records).hasSize(2)

                val plugin = pluginRecord(records)
                assertThat(plugin.alias).isEqualTo("pluginEntry")
                assertThat(plugin.id).isEqualTo("com.company.plugin")

                val version = versionRecord(records)
                assertThat(plugin.version).isEqualTo(version)
            }

            @Test
            fun `a bundle`() {
                val entry = BundleEntry
                val records = entry.allRecords()
                assertThat(records).hasSize(5)

                val bundle = bundleRecord(records)
                assertThat(bundle.alias).isEqualTo("bundleEntry")

                val expected = records.filterIsInstance<LibraryRecord>().toSet()
                assertThat(expected).hasSize(3)
                assertThat(bundle.libs).isEqualTo(expected)
            }

            @Test
            fun `an extra bundle`() {
                val entry = ExtraBundleEntry
                val records = entry.allRecords()
                assertThat(records).hasSize(5)

                val bundle = bundleRecord(records)
                assertThat(bundle.alias).isEqualTo("extraBundleEntry-full")

                val expected = records.filterIsInstance<LibraryRecord>().toSet()
                assertThat(expected).hasSize(3)
                assertThat(bundle.libs).isEqualTo(expected)
            }
        }

        @Test
        fun `produce no records if empty`() {
            val entry = EmptyRootEntry
            val records = entry.allRecords()
            assertThat(records).isEmpty()
        }

        @Test
        fun `ask nested entries for records`() {
            val entry = RootEntry
            val records = entry.allRecords()
            assertThat(records).hasSize(5)
        }

        @Test
        fun `use object's name for alias`() {
            val entry = RootEntry
            val record = entry.allRecords().first()
            assertThat(record.alias).isEqualTo("rootEntry")
        }
    }

    @Nested
    inner class `when is nested` {

        @Nested
        inner class `inherit the version from` {

            @Test
            fun `direct parent`() {
                val entry = DirectInheritingEntry
                val records = entry.allRecords()
                assertThat(records).hasSize(2)

                val library = libraryRecord(records)
                assertThat(library.alias).isEqualTo("directInheritingEntry-inheritor")
                assertThat(library.module).isEqualTo("com.company:lib")

                val version = versionRecord(records)
                assertThat(library.version).isEqualTo(version)
            }

            @Test
            fun `indirect parent`() {
                val entry = IndirectInheritingEntry
                val records = entry.allRecords()
                assertThat(records).hasSize(2)

                val library = libraryRecord(records)
                assertThat(library.alias).isEqualTo("indirectInheritingEntry-separator-inheritor")
                assertThat(library.module).isEqualTo("com.company:lib")

                val version = versionRecord(records)
                assertThat(library.version).isEqualTo(version)
            }
        }

        @Test
        fun `throw if the version is needed, but not declared`() {
            val entry = ErroneousEntry
            assertThrows<IllegalStateException> { entry.allRecords() }
        }

        @Test
        fun `not append 'GradlePlugin' suffix for plugins`() {
            val entry = WithPluginEntry
            val records = entry.allRecords()
            assertThat(records).hasSize(2)

            val plugin = pluginRecord(records)
            assertThat(plugin.alias).isEqualTo("withPluginEntry")
            assertThat(plugin.id).isEqualTo("my.plugin")

            val version = versionRecord(records)
            assertThat(plugin.version).isEqualTo(version)
            assertThat(version.alias).isEqualTo("withPluginEntry-gradlePlugin")
        }

        @Test
        fun `reflect nesting in alias`() {
            val entry = RootEntry
            val records = entry.allRecords()
            val expected = setOf(
                VersionRecord("rootEntry", "re0.0.0"),
                VersionRecord("rootEntry-firstChild", "fc0.0.0"),
                VersionRecord("rootEntry-secondChild", "sc0.0.0"),
                VersionRecord("rootEntry-thirdChild", "tc0.0.0"),
                VersionRecord("rootEntry-thirdChild-grandChild", "gc0.0.0"),
            )
            assertThat(records).isEqualTo(expected)
        }

        @Test
        fun `throw on request to produce records`() {
            val entry = OuterEntry.Nested
            assertThrows<IllegalStateException> { entry.allRecords() }
        }
    }
}
