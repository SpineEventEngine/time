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

package io.spine.time;

import io.spine.base.Time;

import java.time.ZonedDateTime;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.base.Time.currentTimeZone;
import static java.util.Objects.requireNonNull;

/**
 * The current time.
 *
 * <p>An instance of {@code Now} always obtains the {@linkplain Time#currentTime() current time} in
 * different formats. The time zone is fixed for a given instance.
 */
public final class Now {

    private final java.time.ZoneId timeZone;

    private Now(java.time.ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Obtains the {@code Now} in the given time zone.
     *
     * @param timeZone
     *         the time zone to obtain time in
     */
    public static Now get(java.time.ZoneId timeZone) {
        checkNotNull(timeZone);
        return new Now(timeZone);
    }

    /**
     * Obtains the {@code Now} in the current time zone provided by {@link Time#currentTimeZone()}.
     *
     * <p>Note that the time zone of the resulting instance will never change, even if
     * the {@link Time#currentTimeZone()} changes.
     */
    public static Now get() {
        var timeZone = currentTimeZone();
        return get(timeZone);
    }

    /**
     * Obtains the {@code Now} in the given time zone.
     *
     * @param timeZone
     *         the time zone to obtain time in
     */
    public static Now get(ZoneId timeZone) {
        checkNotNull(timeZone);
        var id = timeZone.toJavaTime();
        return get(id);
    }

    /**
     * Obtains the {@code Now} with the given zone offset.
     *
     * @param offset
     *         the time zone offset
     * @deprecated please use {@link #get(java.time.ZoneId)} or {@link #get(ZoneId)}
     */
    @Deprecated
    public static Now get(ZoneOffset offset) {
        checkNotNull(offset);
        var jt = ZoneOffsets.toJavaTime(offset);
        return get(jt);
    }

    /**
     * Obtains the current month.
     */
    public Month asMonth() {
        var now = now();
        return Months.of(now.getMonth());
    }

    /**
     * Obtains the current year and month.
     */
    public YearMonth asYearMonth() {
        var now = now();
        return YearMonth.newBuilder()
                .setYear(now.getYear())
                .setMonth(Months.of(now.getMonth()))
                .build();
    }

    /**
     * Obtains the current day of week.
     */
    public DayOfWeek asDayOfWeek() {
        var now = now();
        return DaysOfWeek.of(now.getDayOfWeek());
    }

    /**
     * Obtains the current time as a {@code LocalDate}.
     */
    public LocalDate asLocalDate() {
        var jt = now().toLocalDate();
        return LocalDates.of(jt);
    }

    /**
     * Obtains the current time as a {@code LocalTime}.
     */
    public LocalTime asLocalTime() {
        var jt = now().toLocalTime();
        return LocalTimes.of(jt);
    }

    /**
     * Obtains the current time as a {@code LocalDateTime}.
     */
    public LocalDateTime asLocalDateTime() {
        var jt = now().toLocalDateTime();
        return LocalDateTimes.of(jt);
    }

    /**
     * Obtains the current time as an {@code OffsetTime}.
     *
     * @deprecated please use {@link #asZonedDateTime()}
     */
    @Deprecated
    public OffsetTime asOffsetTime() {
        var now = now();
        return OffsetTime.newBuilder()
                .setOffset(ZoneOffsets.of(now.getOffset()))
                .setTime(LocalTimes.of(now.toLocalTime()))
                .build();
    }

    /**
     * Obtains the current time as an {@code OffsetDateTime}.
     *
     * @deprecated please use {@link #asZonedDateTime()}
     */
    @Deprecated
    public OffsetDateTime asOffsetDateTime() {
        var jt = now().toOffsetDateTime();
        return OffsetDateTimes.of(jt);
    }

    /**
     * Obtains the current time as an {@code ZonedDateTime}.
     */
    public io.spine.time.ZonedDateTime asZonedDateTime() {
        return ZonedDateTimes.of(now());
    }

    private ZonedDateTime now() {
        var time = Time.currentTime();
        var instant = InstantConverter.reversed().convert(time);
        requireNonNull(instant);
        return ZonedDateTime.ofInstant(instant, timeZone);
    }
}
