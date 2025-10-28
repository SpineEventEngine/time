/*
 * Copyright 2025, TeamDev. All rights reserved.
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
import com.google.protobuf.timestamp
import io.kotest.matchers.shouldBe
import io.spine.protobuf.Durations2.add
import io.spine.protobuf.Durations2.nanos
import io.spine.protobuf.Durations2.seconds
import io.spine.time.LocalDate
import io.spine.time.LocalDateTime
import io.spine.time.LocalTime
import io.spine.time.Month as ProtoMonth
import io.spine.time.YearMonth as ProtoYearMonth
import io.spine.time.ZoneId
import kotlinx.datetime.LocalDate as KtLocalDate
import kotlinx.datetime.LocalDateTime as KtLocalDateTime
import kotlinx.datetime.LocalTime as KtLocalTime
import kotlinx.datetime.Month as KtMonth
import kotlinx.datetime.TimeZone as KtTimeZone
import kotlinx.datetime.YearMonth as KtYearMonth
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Instant
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("KotlinX DateTime conversion extensions should")
internal class KotlinXTimeExtsSpec {

    @Nested
    inner class DurationConversions {
        @Test
        fun toKotlin() {
            val duration: Duration = add(
                seconds(1234),
                nanos(567_000_000)
            )
            duration.toKotlinDuration().inWholeNanoseconds shouldBe
                    1234L * 1_000_000_000L + 567_000_000L
        }

        @Test
        fun fromKotlin() {
            val kDuration = (1234L * 1_000_000_000L + 567_000_000L).nanoseconds
            val proto = kDuration.toProtoDuration()
            proto.seconds shouldBe 1234L
            proto.nanos shouldBe 567_000_000
        }
    }

    @Nested
    inner class TimestampConversions {
        
        @Test
        fun `'toInstant' and back`() {
            val seconds = 1_234L
            val nanos = 567_000_000
            val ts = timestamp {
                this.seconds = seconds
                this.nanos = nanos
            }
            val instant = ts.toInstant()
            instant shouldBe Instant.fromEpochSeconds(seconds, nanos.toLong())
            instant.toTimestamp() shouldBe ts
        }
    }

    @Nested
    inner class MonthConversions {
        
        @Test
        fun `map all months`() {
            KtMonth.entries.forEach { kx ->
                val proto = kx.toProtoMonth()
                proto.toKotlinMonth() shouldBe kx
            }
        }
    }

    @Nested
    inner class YearMonthConversions {

        @Test
        fun roundTrip() {
            val kx = KtYearMonth(2025, KtMonth.OCTOBER)
            val proto: ProtoYearMonth = kx.toProtoYearMonth()
            proto.year shouldBe 2025
            proto.month shouldBe ProtoMonth.OCTOBER
            proto.toKotlinYearMonth() shouldBe kx
        }
    }

    @Nested
    inner class LocalDateConversions {

        @Test
        fun roundTrip() {
            val kx = KtLocalDate(2024, KtMonth.FEBRUARY, 29)
            val proto: LocalDate = kx.toProtoLocalDate()
            proto.year shouldBe 2024
            proto.month shouldBe ProtoMonth.FEBRUARY
            proto.day shouldBe 29
            proto.toKotlinLocalDate() shouldBe kx
        }
    }

    @Nested
    inner class LocalTimeConversions {

        @Test
        fun roundTrip() {
            val kx = KtLocalTime(23, 59, 58, 999_999_999)
            val proto: LocalTime = kx.toProtoLocalTime()
            proto.hour shouldBe 23
            proto.minute shouldBe 59
            proto.second shouldBe 58
            proto.nano shouldBe 999_999_999
            proto.toKotlinLocalTime() shouldBe kx
        }
    }

    @Nested
    inner class LocalDateTimeConversions {

        @Test
        fun roundTrip() {
            val kx = KtLocalDateTime(KtLocalDate(2030, KtMonth.JANUARY, 1), KtLocalTime(0, 0, 0, 1))
            val proto: LocalDateTime = kx.toProtoLocalDateTime()
            proto.date.year shouldBe 2030
            proto.date.month shouldBe ProtoMonth.JANUARY
            proto.date.day shouldBe 1
            proto.time.hour shouldBe 0
            proto.time.minute shouldBe 0
            proto.time.second shouldBe 0
            proto.time.nano shouldBe 1
            proto.toKotlinLocalDateTime() shouldBe kx
        }

        @Test
        fun `missing time defaults to midnight`() {
            val localDate =
                LocalDate.newBuilder().setYear(2021).setMonth(ProtoMonth.MARCH).setDay(14)
            val proto = LocalDateTime.newBuilder()
                .setDate(localDate)
                .build()
            val kx = proto.toKotlinLocalDateTime()
            kx.date shouldBe KtLocalDate(2021, KtMonth.MARCH, 14)
            kx.time shouldBe KtLocalTime(0, 0, 0, 0)
        }
    }

    @Nested
    inner class TimeZoneConversions {

        @Test
        fun roundTrip() {
            val kx = KtTimeZone.of("Europe/Amsterdam")
            val proto: ZoneId = kx.toProtoZoneId()
            proto.value shouldBe "Europe/Amsterdam"
            proto.toKotlinTimeZone() shouldBe kx
        }
    }
}
