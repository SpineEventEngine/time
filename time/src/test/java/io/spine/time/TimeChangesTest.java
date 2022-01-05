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

@DisplayName("`TimeChanges` should")
class TimeChangesTest extends UtilityClassTest<TimeChanges> {

    TimeChangesTest() {
        super(TimeChanges.class);
    }

    @Override
    protected void configure(NullPointerTester tester) {
        super.configure(tester);
        tester.setDefault(LocalDate.class, LocalDate.getDefaultInstance())
              .setDefault(LocalTime.class, LocalTime.getDefaultInstance());
        setDefaultsForDeprecatedToo(tester);
    }

    @SuppressWarnings("deprecation")
    private static void setDefaultsForDeprecatedToo(NullPointerTester tester) {
        tester.setDefault(OffsetTime.class, OffsetTime.getDefaultInstance())
              .setDefault(OffsetDateTime.class, OffsetDateTime.getDefaultInstance());
    }

    @Nested
    @DisplayName("fail to create a change for equal values")
    class RejectEqual {

        @Test
        @DisplayName("`LocalDate`")
        void localDates() {
            var today = Now.get().asLocalDate();
            assertThrows(IllegalArgumentException.class, () -> TimeChanges.of(today, today));
        }

        @Test
        @DisplayName("`LocalTime`")
        void localTimes() {
            var now = Now.get().asLocalTime();
            assertThrows(IllegalArgumentException.class, () -> TimeChanges.of(now, now));
        }

        @Test
        @DisplayName("`OffsetTime`")
        @SuppressWarnings("deprecation")
        void offsetTimes() {
            var inLuxembourg = ZoneOffsets.ofHours(1);
            var now = Now.get(inLuxembourg).asOffsetTime();
            assertThrows(IllegalArgumentException.class, () -> TimeChanges.of(now, now));
        }

        @Test
        @DisplayName("`OffsetDateTime`")
        @SuppressWarnings("deprecation")
        void offsetDateTimes() {
            var inLuxembourg = ZoneOffsets.ofHours(1);
            var now = Now.get(inLuxembourg).asOffsetDateTime();
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
            inKiev = Now.get(java.time.ZoneId.of("GMT+3"));
            inLuxembourg = Now.get(java.time.ZoneId.of("GMT+2"));
        }

        @Test
        @DisplayName("`LocalDate`")
        void forLocalDates() {
            var today = Now.get().asLocalDate();
            var tomorrow = LocalDates.of(today.toJavaTime().plusDays(1));

            var result = TimeChanges.of(today, tomorrow);

            assertEquals(today, result.getPreviousValue());
            assertEquals(tomorrow, result.getNewValue());
        }

        @Test
        @DisplayName("`LocalTime`")
        void forLocalTimes() {
            var inFiveHours = LocalTimes.of(now.toJavaTime().plusHours(5));

            var result = TimeChanges.of(now, inFiveHours);

            assertEquals(now, result.getPreviousValue());
            assertEquals(inFiveHours, result.getNewValue());
        }

        @Test
        @DisplayName("`OffsetTime`")
        @SuppressWarnings("deprecation")
        void forOffsetTimes() {
            var previousTime = inKiev.asOffsetTime();
            var newTime = inLuxembourg.asOffsetTime();

            var result = TimeChanges.of(previousTime, newTime);

            assertEquals(previousTime, result.getPreviousValue());
            assertEquals(newTime, result.getNewValue());
        }

        @Test
        @DisplayName("`OffsetDateTime`")
        @SuppressWarnings("deprecation")
        void forOffsetDateTimes() {
            var previousDateTime = inKiev.asOffsetDateTime();
            var newDateTime = inLuxembourg.asOffsetDateTime();

            var result = TimeChanges.of(previousDateTime, newDateTime);

            assertEquals(previousDateTime, result.getPreviousValue());
            assertEquals(newDateTime, result.getNewValue());
        }
    }
}
