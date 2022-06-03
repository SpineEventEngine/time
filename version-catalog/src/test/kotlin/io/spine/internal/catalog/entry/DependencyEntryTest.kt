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
import io.spine.internal.catalog.entry.given.DependencyEntryTestEnv.Companion.assert
import io.spine.internal.catalog.entry.given.StandaloneDummyVersionDependency
import io.spine.internal.catalog.entry.given.DependencyEntryTestEnv.Companion.bundleRecord
import io.spine.internal.catalog.entry.given.DependencyEntryTestEnv.Companion.libraryRecord
import io.spine.internal.catalog.entry.given.DependencyEntryTestEnv.Companion.versionRecord
import io.spine.internal.catalog.entry.given.ErroneousOuterDummyDependency
import io.spine.internal.catalog.entry.given.LibraryEntryTestEnv.Companion.assert
import io.spine.internal.catalog.entry.given.OuterDummyDependency
import io.spine.internal.catalog.entry.given.SimpleDependency
import io.spine.internal.catalog.entry.given.StandaloneDummyBundleDependency
import io.spine.internal.catalog.entry.given.StandaloneDummyLibraryDependency
import io.spine.internal.catalog.entry.given.StandaloneDummyPropertyDependency
import io.spine.internal.catalog.entry.given.VersionEntryTestEnv.Companion.assert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("`DependencyEntry` should when")
internal class DependencyEntryTest {

    @Nested
    inner class standalone {

        @Test
        fun `assemble a version record, if version is specified`() {
            val version = versionRecord(StandaloneDummyVersionDependency)
            version.assert(
                alias = "standaloneDummyVersionDependency",
                version = "sdvd-0.0.1"
            )
        }

        @Test
        fun `assemble a library record, if module is specified`() {
            val library = libraryRecord(StandaloneDummyLibraryDependency)
            library.assert(
                alias = "standaloneDummyLibraryDependency",
                module = "org.dummy",
                versionRef = "standaloneDummyLibraryDependency"
            )
        }

        @Test
        fun `assemble a bundle record, if bundle is specified`() {
            val bundle = bundleRecord(StandaloneDummyBundleDependency)
            bundle.assert(
                alias = "standaloneDummyBundleDependency",
                libs = setOf(
                    "standaloneDummyBundleDependency-subLib1",
                    "standaloneDummyBundleDependency-subLib2",
                    "standaloneDummyBundleDependency-subLib3"
                )
            )
        }

        @Test
        fun `add an extra library by property delegation`() {
            val library = libraryRecord(StandaloneDummyPropertyDependency)
            library.assert(
                alias = "standaloneDummyPropertyDependency-subLib",
                module = "org.dummy:subLib",
                versionRef = "standaloneDummyPropertyDependency"
            )
        }

        @Test
        fun `don't append extra-lib's name to alias, when it named after the entry`() {
            val library = libraryRecord(SimpleDependency)
            library.assert(
                alias = "simpleDependency",
                module = "org.simple",
                versionRef = "simpleDependency"
            )
        }
    }

    @Nested
    inner class nested {

        @Test
        fun `be able to inherit the version from the outer entry`() {
            val library = libraryRecord(OuterDummyDependency.Nested)
            library.assert(
                alias = "outerDummyDependency-nested",
                module = "org.nested",
                versionRef = "outerDummyDependency"
            )
        }

        @Test
        fun `throw an exception when the version is neither declared nor inherited`() {
            val exception = assertThrows<IllegalStateException> {
                val entry = ErroneousOuterDummyDependency.Nested
                entry.allRecords()
            }
            assertThat(exception.message).isEqualTo(
                "Specify version in this entry or the outer entry!"
            )
        }
    }
}
