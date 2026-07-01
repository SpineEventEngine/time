/*
 * Copyright 2026, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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

@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.spine.time.kotlin

import com.google.protobuf.Duration
import com.google.protobuf.Timestamp
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.spine.time.LocalDate
import io.spine.time.LocalDateTime
import io.spine.time.LocalTime
import io.spine.time.ZoneId
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import io.spine.time.Month as ProtoMonth
import io.spine.time.YearMonth as ProtoYearMonth
import kotlin.time.Duration as KtDuration
import kotlin.time.Instant as KtInstant
import kotlinx.datetime.LocalTime as KtLocalTime

@DisplayName("Conversion of a default value to KotlinX should")
internal class DefaultValueConversionSpec {

    /**
     * These conversions are impossible because a default value carries no
     * meaningful month, date, or zone, so the converter rejects it.
     */
    @Nested
    inner class `reject the default of` {

        @Test
        fun localDate() {
            shouldThrow<IllegalStateException> {
                LocalDate.getDefaultInstance().toKotlinLocalDate()
            }
        }

        @Test
        fun localDateTime() {
            shouldThrow<IllegalStateException> {
                LocalDateTime.getDefaultInstance().toKotlinLocalDateTime()
            }
        }

        @Test
        fun yearMonth() {
            shouldThrow<IllegalStateException> {
                ProtoYearMonth.getDefaultInstance().toKotlinYearMonth()
            }
        }

        @Test
        fun zoneId() {
            shouldThrow<IllegalStateException> {
                ZoneId.getDefaultInstance().toKotlinTimeZone()
            }
        }

        @Test
        fun month() {
            shouldThrow<IllegalStateException> {
                ProtoMonth.MONTH_UNDEFINED.toKotlinMonth()
            }
        }
    }

    /**
     * These conversions are meaningful even for a default value: it maps onto
     * the epoch, a zero duration, or midnight, so the converter is expected to
     * succeed.
     */
    @Nested
    inner class `convert the default of` {

        @Test
        fun `Timestamp to the epoch`() {
            Timestamp.getDefaultInstance().toInstant() shouldBe KtInstant.fromEpochSeconds(0, 0)
        }

        @Test
        fun `Duration to zero`() {
            Duration.getDefaultInstance().toKotlinDuration() shouldBe KtDuration.ZERO
        }

        @Test
        fun `LocalTime to midnight`() {
            LocalTime.getDefaultInstance().toKotlinLocalTime() shouldBe KtLocalTime(0, 0)
        }
    }
}
