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

package io.spine.time;

import com.google.common.testing.NullPointerTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.base.Time.currentTimeZone;
import static io.spine.time.Asserts.assertDatesEqual;
import static io.spine.time.Month.JULY;
import static io.spine.time.testing.TimeTests.avoidDayEdge;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("LocalDateTimes should")
class LocalDateTimesTest
        extends AbstractDateTimeUtilityTest<LocalDateTime, java.time.LocalDateTime> {

    LocalDateTimesTest() {
        super(LocalDateTimes.class,
              Now::asLocalDateTime,
              LocalDateTimes::toString,
              LocalDateTimes::parse,
              LocalDateTimes.converter());
    }

    @Override
    void addDefaults(NullPointerTester nullTester) {
        nullTester.setDefault(LocalTime.class, LocalTime.getDefaultInstance())
                  .setDefault(LocalDate.class, LocalDate.getDefaultInstance());

    }

    @Nested
    @DisplayName("Create new instance by")
    class Create {

        @Test
        @DisplayName("passed date and time")
        void dateTime() {
            LocalDate date = LocalDates.of(1969, JULY, 16);
            LocalTime time = LocalTimes.of(20, 17);

            LocalDateTime dateTime = LocalDateTimes.of(date, time);

            assertEquals(date, dateTime.getDate());
            assertEquals(time, dateTime.getTime());
        }

        @Test
        @DisplayName("current date-time")
        void currentDateTime() {
            avoidDayEdge();
            LocalDateTime now = current();
            // Check that the date is the same. It's safe as we've not passed the end of the day.
            assertDatesEqual(java.time.LocalDate.now(currentTimeZone()), now.getDate());
            // We don't compare time here as it's surely changed.
        }
    }

    @Nested
    @DisplayName("Reject")
    class Arguments {

        @Test
        @DisplayName("default date")
        void defaultDate() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> LocalDateTimes.of(LocalDate.getDefaultInstance(), Now.get().asLocalTime())
            );
        }
    }

    @Test
    @DisplayName("accept midnight time")
    void midnight() {
        LocalDate today = Now.get().asLocalDate();
        LocalTime midnight = LocalTimes.parse("00:00:00");

        LocalDateTime todayMidnight = LocalDateTimes.of(today, midnight);

        assertEquals(today, todayMidnight.getDate());
        assertEquals(midnight, todayMidnight.getTime());
    }
}
