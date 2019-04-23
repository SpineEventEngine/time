/*
 * Copyright 2019, TeamDev. All rights reserved.
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

import com.google.protobuf.Timestamp;
import io.spine.base.Time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.base.Time.currentTimeZone;

/**
 * The current time.
 */
final class Now {

    private final ZoneId timeZone;

    private Now(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    static Now inCurrentTimeZone() {
        ZoneId timeZone = currentTimeZone();
        return in(timeZone);
    }

    static Now in(ZoneId timeZone) {
        checkNotNull(timeZone);
        return new Now(timeZone);
    }

    Month asMonth() {
        ZonedDateTime now = now();
        return Months.of(now.getMonth());
    }

    YearMonth asYearMonth() {
        ZonedDateTime now = now();
        return YearMonth
                .vBuilder()
                .setYear(now.getYear())
                .setMonth(Months.of(now.getMonth()))
                .build();
    }

    DayOfWeek asDayOfWeek() {
        ZonedDateTime now = now();
        return DaysOfWeek.of(now.getDayOfWeek());
    }

    LocalDate asLocalDate() {
        java.time.LocalDate jt = now().toLocalDate();
        return LocalDates.of(jt);
    }

    LocalTime asLocalTime() {
        java.time.LocalTime jt = now().toLocalTime();
        return LocalTimes.of(jt);
    }

    LocalDateTime asLocalDateTime() {
        java.time.LocalDateTime jt = now().toLocalDateTime();
        return LocalDateTimes.of(jt);
    }

    OffsetTime asOffsetTime() {
        ZonedDateTime now = now();
        return OffsetTime
                .vBuilder()
                .setOffset(ZoneOffsets.of(now.getOffset()))
                .setTime(LocalTimes.of(now.toLocalTime()))
                .build();
    }

    OffsetDateTime asOffsetDateTime() {
        java.time.OffsetDateTime jt = now().toOffsetDateTime();
        return OffsetDateTimes.of(jt);
    }

    io.spine.time.ZonedDateTime asZonedDateTime() {
        return ZonedDateTimes.of(now());
    }

    private ZonedDateTime now() {
        Timestamp time = Time.currentTime();
        Instant instant = InstantConverter.reversed()
                                          .convert(time);
        checkNotNull(instant);
        return ZonedDateTime.ofInstant(instant, timeZone);
    }
}
