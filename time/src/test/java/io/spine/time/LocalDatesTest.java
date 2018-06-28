/*
 * Copyright 2018, TeamDev. All rights reserved.
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

import static io.spine.time.Asserts.assertDatesEqual;
import static io.spine.time.LocalDates.checkDate;
import static io.spine.time.LocalDates.toJavaTime;
import static io.spine.time.testing.TimeTests.avoidDayEdge;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("ClassCanBeStatic")
@DisplayName("LocalDates should")
class LocalDatesTest extends AbstractDateTimeUtilityTest<LocalDate, java.time.LocalDate> {

    LocalDatesTest() {
        super(LocalDates.class,
              LocalDates::now,
              LocalDates::toString,
              LocalDates::parse,
              LocalDates.converter());
    }

    @Override
    void addDefaults(NullPointerTester nullTester) {
        nullTester.setDefault(LocalDate.class, LocalDates.now())
                  .setDefault(int.class, 1);

    }

    @Test
    @DisplayName("obtain current date")
    void obtainCurrentDate() {
        avoidDayEdge();
        LocalDate today = LocalDates.now();
        java.time.LocalDate jt = java.time.LocalDate.now();
        assertDatesEqual(jt, today);
    }

    @Nested
    @DisplayName("Create new instance by")
    class Create {

        @Test
        @DisplayName("year, month, and day")
        void byYearMonthDay() {
            int year = 2014;
            Month month = Month.JULY;
            int day = 20;

            LocalDate localDate = LocalDates.of(year, month, day);

            assertEquals(year, localDate.getYear());
            assertEquals(month, localDate.getMonth());
            assertEquals(day, localDate.getDay());
        }

        @Test
        @DisplayName("Java Time value")
        void byJavaTime() {
            java.time.LocalDate jt = java.time.LocalDate.now();
            LocalDate value = LocalDates.of(jt);
            assertDatesEqual(jt, value);
        }
    }

    @Nested
    @DisplayName("Check")
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
    @DisplayName("Reject")
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
                    () -> toJavaTime(LocalDate.getDefaultInstance())
            );
        }

        @Test
        @DisplayName("default value in String conversion")
        void defaultForString() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> LocalDates.toString(LocalDate.getDefaultInstance())
            );
        }
    }
}
