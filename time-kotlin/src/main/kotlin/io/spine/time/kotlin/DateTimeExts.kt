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

import com.google.protobuf.Timestamp
import com.google.protobuf.timestamp
import com.google.protobuf.duration
import kotlin.time.Duration.Companion.nanoseconds
import io.spine.time.localDate
import io.spine.time.localDateTime
import io.spine.time.localTime
import io.spine.time.yearMonth
import io.spine.time.zoneId
import com.google.protobuf.Duration as ProtoDuration
import io.spine.time.LocalDate as ProtoLocalDate
import io.spine.time.LocalDateTime as ProtoLocalDateTime
import io.spine.time.LocalTime as ProtoLocalTime
import io.spine.time.Month as ProtoMonth
import io.spine.time.YearMonth as ProtoYearMonth
import io.spine.time.ZoneId as ProtoZoneId
import kotlin.time.Duration as KtDuration
import kotlin.time.Instant as KtInstant
import kotlinx.datetime.LocalDate as KtLocalDate
import kotlinx.datetime.LocalDateTime as KtLocalDateTime
import kotlinx.datetime.LocalTime as KtLocalTime
import kotlinx.datetime.Month as KtMonth
import kotlinx.datetime.TimeZone as KtTimeZone
import kotlinx.datetime.YearMonth as KtYearMonth

/**
 * Converts this `Timestamp` to KotlinX `Instant`.
 */
public fun Timestamp.toInstant(): KtInstant =
    KtInstant.fromEpochSeconds(this.seconds, this.nanos.toLong())

/**
 * Converts this KotlinX `Instant` to Protobuf `Timestamp`.
 */
public fun KtInstant.toTimestamp(): Timestamp = timestamp {
    seconds = this@toTimestamp.epochSeconds
    nanos = this@toTimestamp.nanosecondsOfSecond
}

private const val NANOS_IN_SECOND = 1_000_000_000L

/**
 * Converts this Protobuf `Duration` to Kotlin `kotlin.time.Duration`.
 */
public fun ProtoDuration.toKotlinDuration(): KtDuration {
    val totalNanos = this.seconds * NANOS_IN_SECOND + this.nanos
    return totalNanos.nanoseconds
}

/**
 * Converts this Kotlin `kotlin.time.Duration` to Protobuf `Duration`.
 */
public fun KtDuration.toProtoDuration(): ProtoDuration {
    val totalNanos = this.inWholeNanoseconds
    val seconds = totalNanos / NANOS_IN_SECOND // truncates toward zero
    val nanos = (totalNanos % NANOS_IN_SECOND).toInt()
    return duration {
        this.seconds = seconds
        this.nanos = nanos
    }
}

/**
 * Converts Spine `Month` to kotlinx.datetime `Month`.
 */
public fun ProtoMonth.toKotlinMonth(): KtMonth = when (this) {
    ProtoMonth.JANUARY -> KtMonth.JANUARY
    ProtoMonth.FEBRUARY -> KtMonth.FEBRUARY
    ProtoMonth.MARCH -> KtMonth.MARCH
    ProtoMonth.APRIL -> KtMonth.APRIL
    ProtoMonth.MAY -> KtMonth.MAY
    ProtoMonth.JUNE -> KtMonth.JUNE
    ProtoMonth.JULY -> KtMonth.JULY
    ProtoMonth.AUGUST -> KtMonth.AUGUST
    ProtoMonth.SEPTEMBER -> KtMonth.SEPTEMBER
    ProtoMonth.OCTOBER -> KtMonth.OCTOBER
    ProtoMonth.NOVEMBER -> KtMonth.NOVEMBER
    ProtoMonth.DECEMBER -> KtMonth.DECEMBER
    ProtoMonth.MONTH_UNDEFINED, ProtoMonth.UNRECOGNIZED ->
        error("Undefined `Month` cannot be converted to `kotlinx.datetime.Month`.")
}

/**
 * Converts kotlinx.datetime `Month` to Spine `Month`.
 */
public fun KtMonth.toProtoMonth(): ProtoMonth = when (this) {
    KtMonth.JANUARY -> ProtoMonth.JANUARY
    KtMonth.FEBRUARY -> ProtoMonth.FEBRUARY
    KtMonth.MARCH -> ProtoMonth.MARCH
    KtMonth.APRIL -> ProtoMonth.APRIL
    KtMonth.MAY -> ProtoMonth.MAY
    KtMonth.JUNE -> ProtoMonth.JUNE
    KtMonth.JULY -> ProtoMonth.JULY
    KtMonth.AUGUST -> ProtoMonth.AUGUST
    KtMonth.SEPTEMBER -> ProtoMonth.SEPTEMBER
    KtMonth.OCTOBER -> ProtoMonth.OCTOBER
    KtMonth.NOVEMBER -> ProtoMonth.NOVEMBER
    KtMonth.DECEMBER -> ProtoMonth.DECEMBER
}

/**
 * Converts Spine `YearMonth` to kotlinx.datetime `YearMonth`.
 */
public fun ProtoYearMonth.toKotlinYearMonth(): KtYearMonth =
    KtYearMonth(this.year, this.month.toKotlinMonth())

/**
 * Converts kotlinx.datetime `YearMonth` to Spine `YearMonth`.
 */
public fun KtYearMonth.toProtoYearMonth(): ProtoYearMonth =
    yearMonth {
        year = this@toProtoYearMonth.year
        month = this@toProtoYearMonth.month.toProtoMonth()
    }

/**
 * Converts Spine `LocalDate` to kotlinx.datetime `LocalDate`.
 */
public fun ProtoLocalDate.toKotlinLocalDate(): KtLocalDate =
    KtLocalDate(this.year, this.month.toKotlinMonth(), this.day)

/**
 * Converts kotlinx.datetime `LocalDate` to Spine `LocalDate`.
 */
public fun KtLocalDate.toProtoLocalDate(): ProtoLocalDate =
    localDate {
        year = this@toProtoLocalDate.year
        month = this@toProtoLocalDate.month.toProtoMonth()
        day = this@toProtoLocalDate.day
    }

/**
 * Converts Spine `LocalTime` to kotlinx.datetime `LocalTime`.
 */
public fun ProtoLocalTime.toKotlinLocalTime(): KtLocalTime =
    KtLocalTime(this.hour, this.minute, this.second, this.nano)

/**
 * Converts kotlinx.datetime `LocalTime` to Spine `LocalTime`.
 */
public fun KtLocalTime.toProtoLocalTime(): ProtoLocalTime =
    localTime {
        hour = this@toProtoLocalTime.hour
        minute = this@toProtoLocalTime.minute
        second = this@toProtoLocalTime.second
        nano = this@toProtoLocalTime.nanosecond
    }

/**
 * Converts Spine `LocalDateTime` to kotlinx.datetime `LocalDateTime`.
 */
public fun ProtoLocalDateTime.toKotlinLocalDateTime(): KtLocalDateTime {
    require(this.hasDate()) { "LocalDateTime.date is required by the proto definition." }
    val kDate = this.date.toKotlinLocalDate()
    val kTime = if (this.hasTime()) this.time.toKotlinLocalTime() else KtLocalTime(0, 0, 0, 0)
    return KtLocalDateTime(kDate, kTime)
}

/**
 * Converts kotlinx.datetime `LocalDateTime` to Spine `LocalDateTime`.
 */
public fun KtLocalDateTime.toProtoLocalDateTime(): ProtoLocalDateTime =
    localDateTime {
        date = this@toProtoLocalDateTime.date.toProtoLocalDate()
        time = this@toProtoLocalDateTime.time.toProtoLocalTime()
    }

/**
 * Converts Spine `ZoneId` to kotlinx.datetime `TimeZone`.
 */
public fun ProtoZoneId.toKotlinTimeZone(): KtTimeZone = KtTimeZone.of(this.value)

/**
 * Converts kotlinx.datetime `TimeZone` to Spine `ZoneId`.
 */
public fun KtTimeZone.toProtoZoneId(): ProtoZoneId =
    zoneId {
        value = id
    }
