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
import io.spine.internal.catalog.entry.given.DependencyEntryTestEnv.Companion.record
import io.spine.internal.catalog.entry.given.MethodDummyDependency
import io.spine.internal.catalog.entry.given.PropertyDummyDependency
import io.spine.internal.catalog.entry.given.StandaloneDummyDependency
import io.spine.internal.catalog.BundleRecord
import io.spine.internal.catalog.LibraryRecord
import io.spine.internal.catalog.VersionRecord
import io.spine.internal.catalog.entry.given.ErroneousDummyDependency
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("`DependencyEntry`")
internal class DependencyEntryTest {

    @Nested
    inner class `when standalone should` {

        @Test
        fun `assemble a bundle record if the bundle is specified`() {
            val record = record(StandaloneDummyDependency)
            record.assert(
                "standaloneDummyDependency",
                setOf(
                    "outerDummyDependency-subLib1",
                    "outerDummyDependency-subLib2",
                    "outerDummyDependency-subLib3",
                )
            )
        }

        @Test
        fun `add additional libraries by methods`() {
            val records = MethodDummyDependency.records()
            val bundleLibs = setOf(
                "methodDummyDependency-subLib1",
                "methodDummyDependency-subLib2",
                "methodDummyDependency-subLib3"
            )
            val expected = setOf(
                VersionRecord(alias = "methodDummyDependency", value = "mdd-0.0.1"),
                BundleRecord(alias = "methodDummyDependency", libs = bundleLibs),
                LibraryRecord(
                    alias = "methodDummyDependency-subLib1",
                    module = "org.dummy:subLib1",
                    versionRef = "methodDummyDependency"
                ),
                LibraryRecord(
                    alias = "methodDummyDependency-subLib2",
                    module = "org.dummy:subLib2",
                    versionRef = "methodDummyDependency"
                ),
                LibraryRecord(
                    alias = "methodDummyDependency-subLib3",
                    module = "org.dummy:subLib3",
                    versionRef = "methodDummyDependency"
                ),
            )
            assertThat(records).isEqualTo(expected)
        }

        @Test
        fun `add additional libraries and bundles by property delegation`() {
            val records = PropertyDummyDependency.records()
            val bundleLibs = setOf(
                "propertyDummyDependency-subLib1",
                "propertyDummyDependency-subLib2",
                "propertyDummyDependency-subLib3"
            )
            val expected = setOf(
                VersionRecord(alias = "propertyDummyDependency", value = "pdd-0.0.1"),
                BundleRecord(alias = "propertyDummyDependency-pile", libs = bundleLibs),
                LibraryRecord(
                    alias = "propertyDummyDependency-subLib1",
                    module = "org.dummy:subLib1",
                    versionRef = "propertyDummyDependency"
                ),
                LibraryRecord(
                    alias = "propertyDummyDependency-subLib2",
                    module = "org.dummy:subLib2",
                    versionRef = "propertyDummyDependency"
                ),
                LibraryRecord(
                    alias = "propertyDummyDependency-subLib3",
                    module = "org.dummy:subLib3",
                    versionRef = "propertyDummyDependency"
                ),
            )
            assertThat(records).isEqualTo(expected)
        }

        @Test
        fun `prohibit a sub-library named the same as outer entry`() {
            Assertions.assertThrows(ExceptionInInitializerError::class.java) {
                ErroneousDummyDependency.allRecords()
            }
        }
    }
}
