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

package io.spine.internal.catalog.entry.library

import com.google.common.truth.Truth.assertThat
import io.spine.internal.catalog.entry.library.given.EnclosingDummy
import io.spine.internal.catalog.entry.library.given.LibraryEntryTestEnv.Companion.assert
import io.spine.internal.catalog.entry.library.given.LibraryEntryTestEnv.Companion.record
import io.spine.internal.catalog.entry.library.given.StandaloneDummy
import io.spine.internal.catalog.entry.library.given.WrongStandaloneDummy
import io.spine.internal.catalog.record.LibraryRecord
import io.spine.internal.catalog.record.VersionRecord
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class `LibraryEntry when` {

    @Nested
    inner class `standalone should` {

        @Test
        fun `assemble a record if the module and version are specified`() {
            val record = record(StandaloneDummy)
            record.assert(
                alias = "standaloneDummy",
                module = "org.dummy:dummy-lib",
                versionRef = "standaloneDummy"
            )
        }

        @Test
        fun `fail on a record assembling if the module is specified, but the version not`() {
            assertThrows<IllegalStateException> {
                record(WrongStandaloneDummy)
            }
        }
    }

    @Nested
    inner class `nested should` {

        @Test
        fun `inherit the version from the outer entry`() {
            val records = EnclosingDummy.allRecords()
            val expected = setOf(
                VersionRecord("enclosingDummy", "d-0.0.1"),
                LibraryRecord("enclosingDummy", "org.dummy:dummy-lib", "enclosingDummy"),
                VersionRecord("enclosingDummy-nestedDummy", "d-0.0.1"),
                LibraryRecord("enclosingDummy-nestedDummy", "org.dummy:nested-dummy-lib", "enclosingDummy-nestedDummy"),
            )
            assertThat(records).isEqualTo(expected)
        }
    }
}
