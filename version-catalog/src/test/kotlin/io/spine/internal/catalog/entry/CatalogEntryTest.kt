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
import io.spine.internal.catalog.entry.given.OuterDummy
import io.spine.internal.catalog.entry.given.StandaloneDummy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("`CatalogEntry` should when")
internal class CatalogEntryTest {

    private val standaloneEntry = StandaloneDummy
    private val outerEntry = OuterDummy
    private val nestedEntry = OuterDummy.NestedDummy

    @Nested
    inner class standalone {

        @Test
        fun `use object's name as alias`() {
            assertThat(standaloneEntry.alias).isEqualTo("standaloneDummy")
        }

        @Test
        fun `return null value for outer entry property`() {
            assertThat(standaloneEntry.outerEntry).isNull()
        }
    }

    @Nested
    inner class nested {

        @Test
        fun `regard outer object in alias`() {
            assertThat(nestedEntry.alias).isEqualTo("outerDummy-nestedDummy")
        }

        @Test
        fun `return outer object for outer entry property`() {
            assertThat(nestedEntry.outerEntry).isEqualTo(outerEntry)
        }
    }

    @Nested
    inner class outer {

        @Test
        fun `ask nested entries for records`() {
            assertThat(nestedEntry.wasAsked).isFalse()
            assertThat(outerEntry.allRecords()).isEmpty()
            assertThat(nestedEntry.wasAsked).isTrue()
        }
    }
}