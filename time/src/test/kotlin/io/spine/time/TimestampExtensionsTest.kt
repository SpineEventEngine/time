/*
 * Copyright 2021, TeamDev. All rights reserved.
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

package io.spine.time

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.extensions.proto.ProtoTruth.assertThat
import com.google.protobuf.Timestamp
import com.google.protobuf.util.Timestamps
import com.google.protobuf.util.Timestamps.between
import com.google.protobuf.util.Timestamps.toMicros
import com.google.protobuf.util.Timestamps.toMillis
import com.google.protobuf.util.Timestamps.toNanos
import io.spine.time.given.ImportantTimes.future
import io.spine.time.given.ImportantTimes.inBetween
import io.spine.time.given.ImportantTimes.past
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class `Timestamp extensions should` {

    private val past: Timestamp = past().toTimestamp()
    private val inBetween: Timestamp = inBetween().toTimestamp()
    private val future: Timestamp = future().toTimestamp()

    @Test
    fun `check if the instance is valid`() {
        val invalid = Timestamp.newBuilder().setNanos(-1).build()
        assertFalse(invalid.isValid())
        assertTrue(future.isValid())
    }

    @Test
    fun `print to string`() = assertThat(future.print()).isEqualTo(Timestamps.toString(future))

    @Nested
    inner class `Convert to` {

        @Test
        fun toMillis() = assertThat(future.toMillis()).isEqualTo(toMillis(future))

        @Test
        fun toMicros() = assertThat(future.toMicros()).isEqualTo(toMicros(future))

        @Test
        fun toNanos() = assertThat(past.toNanos()).isEqualTo(toNanos(past))

        @Test
        fun instant() = assertThat(past.toInstant()).isEqualTo(past().toInstant())
    }

    @Nested
    inner class `Tell if this time is` {

        @Test
        fun `before another`() {
            assertTrue(past.isBefore(future))
            assertFalse(future.isBefore(past))
            assertFalse(past.isBefore(past))
        }

        @Test
        fun `after another`() {
            assertTrue(future.isAfter(past))
            assertFalse(past.isAfter(future))
            assertFalse(future.isAfter(future))
        }

        @Test
        fun `between two other`() {
            assertTrue(inBetween.isBetween(past, future))
            assertFalse(past.isBetween(inBetween, future))
        }
    }

    @Nested
    inner class `Provide operators` {

        @Test
        fun compareTo() {
            assertTrue(past < future)
            assertTrue(future > past)
            assertFalse(past > future)
            assertFalse(future < past)
            assertTrue(future == future)
        }

        @Test
        fun `minus 'Timestamp' calculating distance`() {
            val distance = inBetween - past
            val expected = between(inBetween, past)
            assertThat(distance).isEqualTo(expected)
        }

        @Test
        fun `plus 'Duration'`() {
            val distance = past - inBetween
            val timestamp = past + distance
            assertThat(timestamp).isEqualTo(inBetween)
        }

        @Test
        fun `minus 'Duration'`() {
            val distance = past - future
            val timestamp = future - distance
            assertThat(timestamp).isEqualTo(past)
        }
    }
}
