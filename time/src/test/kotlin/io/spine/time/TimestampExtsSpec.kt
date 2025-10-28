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

package io.spine.time

import com.google.protobuf.Timestamp
import com.google.protobuf.timestamp
import com.google.protobuf.util.Timestamps
import com.google.protobuf.util.Timestamps.between
import com.google.protobuf.util.Timestamps.toMicros
import com.google.protobuf.util.Timestamps.toMillis
import com.google.protobuf.util.Timestamps.toNanos
import io.kotest.matchers.shouldBe
import io.spine.time.given.ImportantTimes.future
import io.spine.time.given.ImportantTimes.inBetween
import io.spine.time.given.ImportantTimes.past
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("`Timestamp` Kotlin extensions should")
internal class TimestampExtsSpec {

    private val past: Timestamp = past().toTimestamp()
    private val inBetween: Timestamp = inBetween().toTimestamp()
    private val future: Timestamp = future().toTimestamp()

    @Test
    fun `check if the instance is valid`() {
        val invalid = timestamp { nanos = -1 }
        invalid.isValid() shouldBe false
        future.isValid() shouldBe true
    }

    @Test
    fun `print to string`() {
        future.print() shouldBe Timestamps.toString(future)
    }

    @Nested
    inner class `Convert to` {

        @Test
        fun toMillis() {
            future.toMillis() shouldBe toMillis(future)
        }

        @Test
        fun toMicros() {
            future.toMicros() shouldBe toMicros(future)
        }

        @Test
        fun toNanos() {
            past.toNanos() shouldBe toNanos(past)
        }

    }

    @Nested
    inner class `Tell if this time is` {

        @Test
        fun `before another`() {
            past.isBefore(future) shouldBe true
            future.isBefore(past) shouldBe false
            past.isBefore(past) shouldBe false
        }

        @Test
        fun `after another`() {
            future.isAfter(past) shouldBe true
            past.isAfter(future) shouldBe false
            future.isAfter(future) shouldBe false
        }

        @Test
        fun `between two other`() {
            inBetween.isBetween(past, future) shouldBe true
            past.isBetween(inBetween, future) shouldBe false
        }
    }

    @Nested
    inner class `Provide operators` {

        @Test
        fun compareTo() {
            (past < future) shouldBe true
            (future > past) shouldBe true
            (past > future) shouldBe false
            (future < past) shouldBe false
            @Suppress("KotlinConstantConditions") // serves as documentation
            (future == future) shouldBe true
        }

        @Test
        fun `minus 'Timestamp' calculating distance`() {
            val distance = inBetween - past
            val expected = between(inBetween, past)
            distance shouldBe expected
        }

        @Test
        fun `plus 'Duration'`() {
            val distance = past - inBetween
            val timestamp = past + distance
            timestamp shouldBe inBetween
        }

        @Test
        fun `minus 'Duration'`() {
            val distance = past - future
            val timestamp = future - distance
            timestamp shouldBe past
        }
    }
}
