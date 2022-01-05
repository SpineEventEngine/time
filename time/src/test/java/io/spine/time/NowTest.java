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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static com.google.common.truth.Truth.assertThat;

@DisplayName("`Now` should")
class NowTest {

    private static final ZoneId JT_NEW_YORK = ZoneId.of("America/New_York");
    private static final io.spine.time.ZoneId NEW_YORK = ZoneIds.of(JT_NEW_YORK);
    @SuppressWarnings("deprecation")
    private static final ZoneOffset NEW_YORK_OFFSET = ZoneOffsets.ofHours(-4);

    abstract static class NowTester {

        private final ZoneId timeZone;

        private java.time.ZonedDateTime frozenTime;
        private Now now;

        NowTester(ZoneId zone) {
            this.timeZone = zone;
        }

        abstract Now captureNow();

        @BeforeEach
        void setUp() {
            frozenTime = java.time.ZonedDateTime.now(timeZone);
            var frozenTimestamp = ZonedDateTimes.of(frozenTime).toTimestamp();
            Time.setProvider(() -> frozenTimestamp);
            now = captureNow();
        }

        @AfterEach
        void tearDown() {
            Time.resetProvider();
        }

        @Test
        @DisplayName("`DayOfWeek`")
        void dayOfWeek() {
            var day = now.asDayOfWeek();
            assertThat(day.getNumber()).isEqualTo(frozenTime.getDayOfWeek()
                                                            .getValue());
        }

        @Test
        @DisplayName("`LocalDate`")
        void localDate() {
            var date = now.asLocalDate();
            assertCorrectDate(date);
        }

        @Test
        @DisplayName("`LocalDateTime`")
        void localDateTime() {
            var dateTime = now.asLocalDateTime();
            assertCorrectDate(dateTime.getDate());
            assertCorrectTime(dateTime.getTime());
        }

        @Test
        @DisplayName("`LocalTime`")
        void localTime() {
            var time = now.asLocalTime();
            assertCorrectTime(time);
        }

        @Test
        @DisplayName("`OffsetDateTime`")
        @SuppressWarnings("deprecation")
        void offsetDateTime() {
            var offsetDateTime = now.asOffsetDateTime();
            var dateTime = offsetDateTime.getDateTime();
            assertCorrectDate(dateTime.getDate());
            assertCorrectTime(dateTime.getTime());
            assertThat(offsetDateTime.getOffset().getAmountSeconds())
                    .isEqualTo(frozenTime.getOffset().getTotalSeconds());
        }

        @Test
        @DisplayName("`OffsetTime`")
        @SuppressWarnings("deprecation")
        void offsetTime() {
            var offsetTime = now.asOffsetTime();
            var time = offsetTime.getTime();
            assertCorrectTime(time);
            assertThat(offsetTime.getOffset()
                                 .getAmountSeconds())
                    .isEqualTo(frozenTime.getOffset()
                                         .getTotalSeconds());
        }

        @Test
        @DisplayName("`YearMonth`")
        void yearMonth() {
            var yearMonth = now.asYearMonth();
            assertThat(yearMonth.getYear()).isEqualTo(frozenTime.getYear());
            assertThat(yearMonth.getMonthValue()).isEqualTo(frozenTime.getMonthValue());
        }

        @Test
        @DisplayName("ZonedDateTime")
        void zonedDateTime() {
            var zonedDateTime = now.asZonedDateTime();
            var dateTime = zonedDateTime.getDateTime();
            assertCorrectDate(dateTime.getDate());
            assertCorrectTime(dateTime.getTime());
            assertThat(zonedDateTime.getZone().getValue())
                    .isEqualTo(frozenTime.getZone().getId());
        }

        private void assertCorrectDate(LocalDate date) {
            assertThat(date.getDay()).isEqualTo(frozenTime.getDayOfMonth());
            assertThat(date.getMonth()
                           .getNumber()).isEqualTo(frozenTime.getMonthValue());
            assertThat(date.getYear()).isEqualTo(frozenTime.getYear());
        }

        private void assertCorrectTime(LocalTime time) {
            assertThat(time.hour()).isEqualTo(frozenTime.getHour());
            assertThat(time.minute()).isEqualTo(frozenTime.getMinute());
            assertThat(time.second()).isEqualTo(frozenTime.getSecond());
            assertThat(time.nano()).isEqualTo(frozenTime.getNano());
        }
    }

    @Nested
    @DisplayName("with the system `ZoneId` should obtain current")
    class Current extends NowTester {

        Current() {
            super(Time.currentTimeZone());
        }

        @Override
        Now captureNow() {
            return Now.get();
        }
    }

    @Nested
    @DisplayName("with the given `java.time.ZoneId` should obtain current")
    class JtZoned extends NowTester {

        JtZoned() {
            super(JT_NEW_YORK);
        }

        @Override
        Now captureNow() {
            return Now.get(JT_NEW_YORK);
        }
    }

    @Nested
    @DisplayName("with the given `ZoneId` should obtain current")
    class Zoned extends NowTester {

        Zoned() {
            super(NEW_YORK.toJavaTime());
        }

        @Override
        Now captureNow() {
            return Now.get(NEW_YORK);
        }
    }

    @Nested
    @DisplayName("with the given `ZoneOffset` should obtain current")
    @SuppressWarnings("deprecation")
    class Offset extends NowTester {

        Offset() {
            super(ZoneOffsets.toJavaTime(NEW_YORK_OFFSET));
        }

        @Override
        Now captureNow() {
            return Now.get(NEW_YORK_OFFSET);
        }
    }
}
