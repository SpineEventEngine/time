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
import io.spine.testing.UtilityClassTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeChangesTest extends UtilityClassTest<TimeChanges> {

    TimeChangesTest() {
        super(TimeChanges.class);
    }

    @Override
    protected void configure(NullPointerTester tester) {
        super.configure(tester);
        tester.setDefault(OffsetTime.class, OffsetTime.getDefaultInstance())
              .setDefault(OffsetDateTime.class, OffsetDateTime.getDefaultInstance())
              .setDefault(LocalDate.class, LocalDate.getDefaultInstance())
              .setDefault(LocalTime.class, LocalTime.getDefaultInstance());
    }

    @Nested
    @DisplayName("fail to create a change for equal values")
    class RejectEqual {

        @Test
        @DisplayName("LocalDate")
        void localDates() {
            LocalDate today = Now.get().asLocalDate();
            assertThrows(IllegalArgumentException.class, () -> TimeChanges.of(today, today));
        }

        @Test
        @DisplayName("LocalTime")
        void localTimes() {
            LocalTime now = Now.get().asLocalTime();
            assertThrows(IllegalArgumentException.class, () -> TimeChanges.of(now, now));
        }

        @Test
        @DisplayName("OffsetTime")
        void offsetTimes() {
            ZoneOffset inLuxembourg = ZoneOffsets.ofHours(1);
            OffsetTime now = Now.get(inLuxembourg).asOffsetTime();
            assertThrows(IllegalArgumentException.class, () -> TimeChanges.of(now, now));
        }

        @Test
        @DisplayName("OffsetDateTime")
        void offsetDateTimes() {
            ZoneOffset inLuxembourg = ZoneOffsets.ofHours(1);
            OffsetDateTime now = Now.get(inLuxembourg).asOffsetDateTime();
            assertThrows(IllegalArgumentException.class, () -> TimeChanges.of(now, now));
        }
    }

    @Nested
    @DisplayName("create a value of type")
    class Create {

        private LocalTime now;
        private Now inKiev;
        private Now inLuxembourg;

        @BeforeEach
        void setUp() {
            now = Now.get().asLocalTime();
            // Assume it's Summer now.
            inKiev = Now.get(ZoneOffsets.ofHours(3));
            inLuxembourg = Now.get(ZoneOffsets.ofHours(2));
        }

        @Test
        @DisplayName("LocalDate")
        void forLocalDates() {
            LocalDate today = Now.get().asLocalDate();
            LocalDate tomorrow = LocalDates.of(LocalDates.toJavaTime(today)
                                                         .plusDays(1));

            LocalDateChange result = TimeChanges.of(today, tomorrow);

            assertEquals(today, result.getPreviousValue());
            assertEquals(tomorrow, result.getNewValue());
        }

        @Test
        @DisplayName("LocalTime")
        void forLocalTimes() {
            LocalTime inFiveHours = LocalTimes.of(LocalTimes.toJavaTime(now)
                                                            .plusHours(5));

            LocalTimeChange result = TimeChanges.of(now, inFiveHours);

            assertEquals(now, result.getPreviousValue());
            assertEquals(inFiveHours, result.getNewValue());
        }

        @Test
        @DisplayName("OffsetTime")
        void forOffsetTimes() {
            OffsetTime previousTime = inKiev.asOffsetTime();
            OffsetTime newTime = inLuxembourg.asOffsetTime();

            OffsetTimeChange result = TimeChanges.of(previousTime, newTime);

            assertEquals(previousTime, result.getPreviousValue());
            assertEquals(newTime, result.getNewValue());
        }

        @Test
        @DisplayName("OffsetDateTime")
        void forOffsetDateTimes() {
            OffsetDateTime previousDateTime = inKiev.asOffsetDateTime();
            OffsetDateTime newDateTime = inLuxembourg.asOffsetDateTime();

            OffsetDateTimeChange result = TimeChanges.of(previousDateTime, newDateTime);

            assertEquals(previousDateTime, result.getPreviousValue());
            assertEquals(newDateTime, result.getNewValue());
        }
    }
}
