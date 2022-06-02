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

package io.spine.internal.catalog.entry

import com.google.common.truth.Truth.assertThat
import io.spine.internal.catalog.entry.given.ErroneousStandaloneDummyPlugin
import io.spine.internal.catalog.entry.given.LibraryEntryTestEnv.Companion.assert
import io.spine.internal.catalog.entry.given.OuterDummyPlugin
import io.spine.internal.catalog.entry.given.OuterDummyVersion
import io.spine.internal.catalog.entry.given.PluginEntryTestEnv.Companion.assert
import io.spine.internal.catalog.entry.given.PluginEntryTestEnv.Companion.libraryRecord
import io.spine.internal.catalog.entry.given.PluginEntryTestEnv.Companion.pluginRecord
import io.spine.internal.catalog.entry.given.PluginEntryTestEnv.Companion.versionRecord
import io.spine.internal.catalog.entry.given.StandaloneDummyPlugin
import io.spine.internal.catalog.entry.given.VersionEntryTestEnv.Companion.assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("`PluginEntry` should when")
internal class PluginEntryTest {

    @Nested
    inner class standalone {

        @Test
        fun `assemble plugin, library and version records`() {
            val entry = StandaloneDummyPlugin
            assertThat(entry.allRecords()).hasSize(3)

            val version = versionRecord(entry)
            version.assert(
                alias = "standaloneDummyPlugin",
                version = "sdp-0.0.1"
            )

            val library = libraryRecord(entry)
            library.assert(
                alias = "standaloneDummyPlugin",
                module = "org.dummy",
                versionRef = "standaloneDummyPlugin"
            )

            val plugin = pluginRecord(entry)
            plugin.assert(
                alias = "standaloneDummyPlugin",
                id = "dummy-plugin",
                versionRef = "standaloneDummyPlugin"
            )
        }

        @Test
        fun `throw an exception when the version is not specified`() {
            val exception = assertThrows<IllegalStateException> {
                val entry = ErroneousStandaloneDummyPlugin
                entry.allRecords()
            }
            assertThat(exception.message).isEqualTo(
                "Specify version in this entry or the outer entry!"
            )
        }
    }

    @Nested
    inner class nested {

        @Test
        fun `not prepend a 'GradlePlugin' prefix for the plugin alias `() {
            val plugin = pluginRecord(OuterDummyPlugin.GradlePlugin)
            plugin.assert(
                alias = "outerDummyPlugin",
                id = "dummy-gradle-plugin",
                versionRef = "outerDummyPlugin-gradlePlugin"
            )
        }

        @Test
        fun `be able to inherit the version from the outer entry`() {
            val plugin = pluginRecord(OuterDummyVersion.GradlePlugin)
            plugin.assert(
                alias = "outerDummyVersion",
                id = "dummy-gradle-plugin",
                versionRef = "outerDummyVersion"
            )
        }

        @Test
        fun `throw an exception when the version is neither declared nor inherited`() {
            val exception = assertThrows<IllegalStateException> {
                val entry = OuterDummyPlugin.ErroneousGradlePlugin
                entry.allRecords()
            }
            assertThat(exception.message).isEqualTo(
                "Specify version in this entry or the outer entry!"
            )
        }
    }
}
