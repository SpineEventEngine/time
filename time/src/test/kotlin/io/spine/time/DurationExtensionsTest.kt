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
import com.google.protobuf.Duration
import com.google.protobuf.util.Durations
import com.google.protobuf.util.Durations.toDays
import com.google.protobuf.util.Durations.toHours
import com.google.protobuf.util.Durations.toMicros
import com.google.protobuf.util.Durations.toMillis
import com.google.protobuf.util.Durations.toMinutes
import com.google.protobuf.util.Durations.toNanos
import com.google.protobuf.util.Durations.toSeconds
import com.google.protobuf.util.Durations.toString
import io.spine.protobuf.Durations2
import io.spine.protobuf.Durations2.ZERO
import io.spine.protobuf.Durations2.add
import io.spine.protobuf.Durations2.hours
import io.spine.protobuf.Durations2.minutes
import io.spine.protobuf.Durations2.nanos
import io.spine.protobuf.Durations2.seconds
import io.spine.protobuf.Durations2.toJavaTime
import io.spine.testing.TestValues.random
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class `Duration extensions should` {

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
        fun isValid() = assertTrue(duration.isValid())

        @Test
        fun isNegative() {
            assertFalse(duration.isNegative())
            assertTrue(hours(-1).isNegative())
        }

        @Test
        fun isPositive() {
            assertTrue(duration.isPositive())
            assertFalse(minutes(-5).isPositive())
        }

        @Test
        fun isPositiveOrZero() {
            assertTrue(duration.isPositiveOrZero())
            assertTrue(ZERO.isPositiveOrZero())
            assertFalse(seconds(-100).isPositiveOrZero())
        }

        @Test
        fun isZero() {
            assertFalse(duration.isZero())
            assertTrue(ZERO.isZero())
        }
    }

    @Test
    fun `print to string`() =
        assertThat(duration.print()).isEqualTo(toString(duration))

    @Nested
    inner class `Convert to` {

        @Test
        fun toDays() = assertThat(duration.toDays()).isEqualTo(toDays(duration))

        @Test
        fun toHours() = assertThat(duration.toHours()).isEqualTo(toHours(duration))

        @Test
        fun toMinutes() = assertThat(duration.toMinutes()).isEqualTo(toMinutes(duration))

        @Test
        fun toSeconds() = assertThat(duration.toSeconds()).isEqualTo(toSeconds(duration))

        @Test
        fun toMillis() = assertThat(duration.toMillis()).isEqualTo(toMillis(duration))

        @Test
        fun toMicros() = assertThat(duration.toMicros()).isEqualTo(toMicros(duration))

        @Test
        fun toNanos() = assertThat(duration.toNanos()).isEqualTo(toNanos(duration))

        @Test
        fun toJavaTime() = assertThat(duration.toJavaTime()).isEqualTo(toJavaTime(duration))
    }

    @Nested
    inner class `Provide operators` {

        @Test
        fun compareTo() {
            assertTrue(duration > ZERO)
            assertTrue(ZERO < duration)
            assertTrue(duration == duration)
        }

        @Test
        fun `plus Duration`() = assertThat(duration + duration).isEqualTo(add(duration, duration))
    }
}
