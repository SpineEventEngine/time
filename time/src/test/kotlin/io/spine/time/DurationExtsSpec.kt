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

import com.google.protobuf.Duration
import com.google.protobuf.util.Durations.ZERO
import com.google.protobuf.util.Durations.toDays
import com.google.protobuf.util.Durations.toHours
import com.google.protobuf.util.Durations.toMicros
import com.google.protobuf.util.Durations.toMillis
import com.google.protobuf.util.Durations.toMinutes
import com.google.protobuf.util.Durations.toNanos
import com.google.protobuf.util.Durations.toSeconds
import com.google.protobuf.util.Durations.toString
import io.kotest.matchers.shouldBe
import io.spine.protobuf.Durations2.add
import io.spine.protobuf.Durations2.hours
import io.spine.protobuf.Durations2.minutes
import io.spine.protobuf.Durations2.nanos
import io.spine.protobuf.Durations2.seconds
import io.spine.protobuf.Durations2.toJavaTime
import io.spine.testing.TestValues.random
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("`Duration` Kotlin extensions should")
internal class DurationExtsSpec {

    private lateinit var duration: Duration

    @BeforeEach
    fun generateDuration() {
        duration = add(
            seconds(random(10000).toLong()),
            nanos(random(100000).toLong())
        )
    }

    @Nested
    inner class `Tell if` {

        @Test
        fun isValid() {
            assertTrue(duration.isValid())
        }

        @Test
        fun isNegative() {
            duration.isNegative() shouldBe false
            hours(-1).isNegative() shouldBe true
        }

        @Test
        fun isPositive() {
            duration.isPositive() shouldBe true
            minutes(-5).isPositive() shouldBe false
        }

        @Test
        fun isPositiveOrZero() {
            duration.isPositiveOrZero() shouldBe true
            ZERO.isPositiveOrZero() shouldBe true

            seconds(-100).isPositiveOrZero() shouldBe false
        }

        @Test
        fun isZero() {
            duration.isZero() shouldBe false
            ZERO.isZero() shouldBe true
        }
    }

    @Test
    fun `print to string`() {
        duration.print() shouldBe toString(duration)
    }

    @Nested
    inner class `Convert to` {

        @Test
        fun toDays() {
            duration.toDays() shouldBe toDays(duration)
        }

        @Test
        fun toHours() {
            duration.toHours() shouldBe toHours(duration)
        }

        @Test
        fun toMinutes() {
            duration.toMinutes() shouldBe toMinutes(duration)
        }

        @Test
        fun toSeconds() {
            duration.toSeconds() shouldBe toSeconds(duration)
        }

        @Test
        fun toMillis() {
            duration.toMillis() shouldBe toMillis(duration)
        }

        @Test
        fun toMicros() {
            duration.toMicros() shouldBe toMicros(duration)
        }

        @Test
        fun toNanos() {
            duration.toNanos() shouldBe toNanos(duration)
        }

        @Test
        fun toJavaTime() {
            duration.toJavaTime() shouldBe toJavaTime(duration)
        }
    }

    @Nested
    inner class `Provide operators` {

        @Test
        fun compareTo() {
            (duration > ZERO) shouldBe true
            (ZERO < duration) shouldBe true
            @Suppress("KotlinConstantConditions") // Serves for documentation purposes.
            (duration == duration) shouldBe true
        }

        @Test
        fun `plus Duration`() {
            duration + duration shouldBe add(duration, duration)
        }
    }
}
