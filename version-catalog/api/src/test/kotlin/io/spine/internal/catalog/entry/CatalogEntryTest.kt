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
import org.junit.jupiter.api.assertThrows

@DisplayName("`CatalogEntry` should")
internal class CatalogEntryTest {

    @Nested
    inner class `when standalone` {

        private val standaloneEntry = StandaloneDummy

        @Test
        fun `use object's name as alias`() {
            assertThat(standaloneEntry.alias).isEqualTo("standaloneDummy")
        }

        @Test
        fun `produce no records`() {
            assertThat(standaloneEntry.allRecords()).isEmpty()
        }
    }

    @Nested
    inner class `when nested` {

        @Test
        fun `regard parent entries in alias`() {
            val nested = OuterDummy.Runtime.Mac
            assertThat(nested.alias).isEqualTo("outerDummy-runtime-mac")
        }

        @Test
        fun `throw an exception when being nested in a plain object`() {
            val exception = assertThrows<ExceptionInInitializerError> {
                // Let's trigger object initializing.
                OuterDummy.NotEntry.Api.Params
            }

            val cause = exception.cause
            assertThat(cause).isInstanceOf(IllegalStateException::class.java)
            assertThat(cause).hasMessageThat().isEqualTo("Plain objects can't nest entries!")
        }
    }

    @Nested
    inner class `when outer` {

        @Test
        fun `ask nested entries for records`() {

            val nested = listOf(
                OuterDummy.Runtime,
                OuterDummy.Runtime.Mac,
                OuterDummy.Runtime.Win,
                OuterDummy.Runtime.Linux,
            )

            nested.forEach {
                assertThat(it.wasAsked).isFalse()
            }

            assertThat(OuterDummy.allRecords()).isEmpty()

            nested.forEach {
                assertThat(it.wasAsked).isTrue()
            }
        }
    }
}
