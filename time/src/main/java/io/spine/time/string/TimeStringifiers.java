/*
 * Copyright 2020, TeamDev. All rights reserved.
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

package io.spine.time.string;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Timestamp;
import io.spine.string.Registrar;
import io.spine.string.Stringifier;
import io.spine.string.StringifierRegistry;
import io.spine.time.DayOfWeek;
import io.spine.time.LocalDate;
import io.spine.time.LocalDateTime;
import io.spine.time.LocalTime;
import io.spine.time.Month;
import io.spine.time.OffsetDateTime;
import io.spine.time.OffsetTime;
import io.spine.time.YearMonth;
import io.spine.time.ZoneId;
import io.spine.time.ZoneOffset;
import io.spine.time.ZonedDateTime;

/**
 * A collection of stringifiers for date/time value objects.
 */
public final class TimeStringifiers {

    static {
        Registrar registrar = new Registrar(ImmutableList.of(
                forDayOfWeek(),
                forLocalDate(),
                forLocalDateTime(),
                forLocalTime(),
                forMonth(),
                forOffsetDateTime(),
                forOffsetTime(),
                forYearMonth(),
                forZoneId(),
                forZonedDateTime(),
                forZoneOffset()
        ));
        registrar.register();
    }

    /** Prevent instantiation of this utility class. */
    private TimeStringifiers() {
    }

    /**
     * Obtains default stringifier for {@code DayOfWeek}s.
     *
     * <p>This stringifier is automatically registered in the
     * {@link StringifierRegistry StringifierRegistry}.
     */
    public static Stringifier<DayOfWeek> forDayOfWeek() {
        return DayOfWeekStringifier.instance();
    }

    /**
     * Obtains a stringifier for IDs based on {@code Timestamp}s.
     *
     * <p>The stringifier replaces colons in time part of a string representation of a timestamp.
     *
     * <p>For example, the following string:
     * <pre>
     * "1973-01-01T23:59:59.999999999Z"
     * </pre>
     * would be converted to:
     * <pre>
     * "1973-01-01T23-59-59.999999999Z"
     * </pre>
     *
     * <p>This stringifier can be convenient for storing IDs based on {@code Timestamp}s.
     */
    public static Stringifier<Timestamp> forTimestampWebSafe() {
        return WebSafeTimestampStringifier.instance();
    }

    /**
     * Obtains default stringifier for local dates.
     *
     * <p>The stringifier uses {@code yyyy-MM-dd} format for dates.
     *
     * <p>This stringifier is automatically registered in the
     * {@link StringifierRegistry StringifierRegistry}.
     *
     * @see io.spine.time.LocalDates#toString(LocalDate) LocalDates.toString(LocalDate)
     * @see io.spine.time.LocalDates#parse(String) LocalDates.parse(String)
     */
    public static Stringifier<LocalDate> forLocalDate() {
        return LocalDateStringifier.instance();
    }

    /**
     * Obtains default stringifier for local date-time values in ISO-8601 formats.
     *
     * <p>This stringifier is automatically registered in the
     * {@link StringifierRegistry StringifierRegistry}.
     *
     * @see io.spine.time.LocalDateTimes#toString(LocalDateTime)
     *      LocalDateTimes.toString(LocalDateTime)
     * @see io.spine.time.LocalDateTimes#parse(String)
     *      LocalDateTimes.parse(String)
     */
    public static Stringifier<LocalDateTime> forLocalDateTime() {
        return LocalDateTimeStringifier.instance();
    }
    /**
     * Obtains default stringifier for {@code LocalTime} values.
     *
     * <p>This stringifier is automatically registered in the
     * {@link StringifierRegistry StringifierRegistry}.
     *
     * @see io.spine.time.LocalTimes#toString(LocalTime) LocalTimes.toString(LocalTime)
     * @see io.spine.time.LocalTimes#parse(String) LocalTimes.parse(String)
     */
    public static Stringifier<LocalTime> forLocalTime() {
        return LocalTimeStringifier.instance();
    }

    /**
     * Obtains a stringifier for {@code Month} values.
     *
     * <p>This stringifier is automatically registered in the
     * {@link StringifierRegistry StringifierRegistry}.
     *
     * @see io.spine.time.Months#toString(Month) Months.toString(Month)
     * @see io.spine.time.Months#parse(String) Months.parse(String)
     */
    public static Stringifier<Month> forMonth() {
        return MonthStringifier.instance();
    }

    /**
     * Obtains a stringifier for {@code OffsetDateTime} values.
     *
     * <p>This stringifier is automatically registered in the
     * {@link StringifierRegistry StringifierRegistry}.
     *
     * @see io.spine.time.OffsetDateTimes#toString(OffsetDateTime)
     *      OffsetDateTimes.toString(OffsetDateTime)
     * @see io.spine.time.OffsetDateTimes#parse(String)
     *      OffsetDateTimes.parse(String)
     */
    public static Stringifier<OffsetDateTime> forOffsetDateTime() {
        return OffsetDateTimeStringifier.instance();
    }

    /**
     * Obtains default stringifier for {@code OffsetTime} values.
     *
     * <p>This stringifier is automatically registered in the
     * {@link StringifierRegistry StringifierRegistry}.
     *
     * @see io.spine.time.OffsetTimes#toString(OffsetTime) OffsetTimes.toString(OffsetTime)
     * @see io.spine.time.OffsetTimes#parse(String) OffsetTimes.parse(String)
     */
    public static Stringifier<OffsetTime> forOffsetTime() {
        return OffsetTimeStringifier.instance();
    }


    /**
     * Obtains default stringifier for {@code YearMonth} values.
     *
     * <p>This stringifier is automatically registered in the
     * {@link StringifierRegistry StringifierRegistry}.
     *
     * @see io.spine.time.YearMonths#toString(YearMonth) YearMonths.toString(YearMonth)
     * @see io.spine.time.YearMonths#parse(String) YearMonths.parse(String)
     */
    public static Stringifier<YearMonth> forYearMonth() {
        return YearMonthStringifier.instance();
    }

    /**
     * Obtains default stringifier for {@code ZoneId}s.
     *
     * <p>This stringifier is automatically registered in the
     * {@link StringifierRegistry StringifierRegistry}.
     *
     * @see io.spine.time.ZoneIds#toString(ZoneId) ZoneIds.toString(ZoneId)
     * @see io.spine.time.ZoneIds#parse(String) ZoneIds.parse(String)
     */
    public static Stringifier<ZoneId> forZoneId() {
        return ZoneIdStringifier.instance();
    }

    /**
     * Obtains default stringifier for {@code ZonedDateTime}.
     *
     * <p>This stringifier is automatically registered in the
     * {@link StringifierRegistry StringifierRegistry}.
     *
     * @see io.spine.time.ZonedDateTimes#toString(ZonedDateTime)
     *      ZonedDateTimes.toString(ZonedDateTime)
     * @see io.spine.time.ZonedDateTimes#parse(String)
     *      ZonedDateTimes.parse(String)
     */
    public static Stringifier<ZonedDateTime> forZonedDateTime() {
        return ZonedDateTimeStringifier.instance();
    }

    /**
     * Obtains default stringifier for {@code ZoneOffset}s.
     *
     * <p>This stringifier is automatically registered in the
     * {@link StringifierRegistry StringifierRegistry}.
     *
     * @see io.spine.time.ZoneOffsets#toString(ZoneOffset) ZoneOffsets.toString(ZoneOffset)
     * @see io.spine.time.ZoneOffsets#parse(String) ZoneOffsets.parse(String)
     */
    public static Stringifier<ZoneOffset> forZoneOffset() {
        return ZoneOffsetStringifier.instance();
    }
}
