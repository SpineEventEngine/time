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

import com.google.common.testing.NullPointerTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static io.spine.base.Time.currentTimeZone;
import static io.spine.time.Asserts.assertDatesEqual;
import static io.spine.time.LocalDates.checkDate;
import static io.spine.time.testing.TimeTests.avoidDayEdge;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("`LocalDates` should")
class LocalDatesTest extends AbstractDateTimeUtilityTest<LocalDate, java.time.LocalDate> {

    LocalDatesTest() {
        super(LocalDates.class,
              Now::asLocalDate,
              LocalDates::toString,
              LocalDates::parse,
              LocalDates.converter());
    }

    @Override
    void addDefaults(NullPointerTester nullTester) {
        nullTester.setDefault(LocalDate.class, current())
                  .setDefault(int.class, 1);

    }

    @Test
    @DisplayName("obtain current date")
    void obtainCurrentDate() {
        avoidDayEdge();
        var today = current();
        var jt = java.time.LocalDate.now(currentTimeZone());
        assertDatesEqual(jt, today);
    }

    @Nested
    @DisplayName("create new instance by")
    class Create {

        @Test
        @DisplayName("year, month, and day")
        void byYearMonthDay() {
            var year = 2014;
            var month = Month.JULY;
            var day = 20;

            var localDate = LocalDates.of(year, month, day);

            assertEquals(year, localDate.getYear());
            assertEquals(month, localDate.getMonth());
            assertEquals(day, localDate.getDay());
        }

        @Test
        @DisplayName("Java Time value")
        void byJavaTime() {
            var jt = java.time.LocalDate.now(currentTimeZone());
            var value = LocalDates.of(jt);
            assertDatesEqual(jt, value);
        }
    }

    @Nested
    @DisplayName("check")
    class Check {

        @Test
        @DisplayName("lower year bound")
        void yearTooLow() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> checkDate(LocalDate.newBuilder()
                                             .setYear(Year.MIN_VALUE - 1)
                                             .build())
            );
        }

        @Test
        @DisplayName("year high bound")
        void yearTooHigh() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> checkDate(LocalDate.newBuilder()
                                             .setYear(Year.MAX_VALUE + 1)
                                             .build())
            );
        }
    }

    @Nested
    @DisplayName("reject")
    class Arguments {

        @Test
        @DisplayName("negative year value")
        void negativeYear() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> LocalDates.of(-1987, Month.AUGUST, 20)
            );
        }

        @Test
        @DisplayName("negative day value")
        void negativeDay() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> LocalDates.of(1987, Month.AUGUST, -20)
            );
        }

        @Test
        @DisplayName("default value in Java Time conversion")
        void defaultForConversion() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> LocalDates.toJavaTime(LocalDate.getDefaultInstance())
            );
        }

        @Test
        @DisplayName("default value in `String` conversion")
        void defaultForString() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> LocalDates.toString(LocalDate.getDefaultInstance())
            );
        }
    }
}
