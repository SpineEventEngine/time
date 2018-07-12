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

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Alexander Yevsyukov
 */
class TimeChangesTest {

    @Test
    void utilityCtor() {
        assertHasPrivateParameterlessCtor(TimeChanges.class);
    }

    @Test
    void nullCheck() {
        ZoneOffset utc = ZoneOffsets.utc();
        new NullPointerTester()
                .setDefault(OffsetTime.class, OffsetTimes.now(utc))
                .setDefault(OffsetDateTime.class, OffsetDateTimes.now(utc))
                .setDefault(LocalDate.class, LocalDates.now())
                .setDefault(LocalTime.class, LocalTimes.now())
                .testAllPublicStaticMethods(TimeChanges.class);
    }

    @Nested
    @DisplayName("fail to create a change for equal values")
    class RejectEqual {

        @Test
        @DisplayName("LocalDate")
        void localDates() {
            final LocalDate today = LocalDates.now();
            assertThrows(IllegalArgumentException.class, () -> TimeChanges.of(today, today));
        }

        @Test
        @DisplayName("LocalTime")
        void localTimes() {
            final LocalTime now = LocalTimes.now();
            assertThrows(IllegalArgumentException.class, () -> TimeChanges.of(now, now));
        }

        @Test
        @DisplayName("OffsetTime")
        void offsetTimes() {
            final ZoneOffset inLuxembourg = ZoneOffsets.ofHours(1);
            final OffsetTime now = OffsetTimes.now(inLuxembourg);
            assertThrows(IllegalArgumentException.class, () -> TimeChanges.of(now, now));
        }

        @Test
        @DisplayName("OffsetDateTime")
        void offsetDateTimes() {
            final ZoneOffset inLuxembourg = ZoneOffsets.ofHours(1);
            final OffsetDateTime now = OffsetDateTimes.now(inLuxembourg);
            assertThrows(IllegalArgumentException.class, () -> TimeChanges.of(now, now));
        }
    }

    @Nested
    @DisplayName("create a value of type")
    class Create {
        @Test
        @DisplayName("LocalDate")
        void forLocalDates() {
            final LocalDate today = LocalDates.now();
            final LocalDate tomorrow = LocalDates.of(LocalDates.toJavaTime(today).plusDays(1));

            final LocalDateChange result = TimeChanges.of(today, tomorrow);

            assertEquals(today, result.getPreviousValue());
            assertEquals(tomorrow, result.getNewValue());
        }

        @Test
        @DisplayName("LocalTime")
        void forLocalTimes() {
            final LocalTime now = LocalTimes.now();
            final LocalTime inFiveHours = LocalTimes.of(LocalTimes.toJavaTime(now)
                                                                  .plusHours(5));

            final LocalTimeChange result = TimeChanges.of(now, inFiveHours);

            assertEquals(now, result.getPreviousValue());
            assertEquals(inFiveHours, result.getNewValue());
        }

        @Test
        @DisplayName("OffsetTime")
        void forOffsetTimes() {
            final ZoneOffset inKiev = ZoneOffsets.ofHours(3);
            final ZoneOffset inLuxembourg = ZoneOffsets.ofHours(1);
            final OffsetTime previousTime = OffsetTimes.now(inKiev);
            final OffsetTime newTime = OffsetTimes.now(inLuxembourg);

            final OffsetTimeChange result = TimeChanges.of(previousTime, newTime);

            assertEquals(previousTime, result.getPreviousValue());
            assertEquals(newTime, result.getNewValue());
        }

        @Test
        @DisplayName("OffsetDateTime")
        void forOffsetDateTimes() {
            final ZoneOffset inKiev = ZoneOffsets.ofHours(3);
            final ZoneOffset inLuxembourg = ZoneOffsets.ofHours(1);
            final OffsetDateTime previousDateTime = OffsetDateTimes.now(inKiev);
            final OffsetDateTime newDateTime = OffsetDateTimes.now(inLuxembourg);

            final OffsetDateTimeChange result = TimeChanges.of(previousDateTime, newDateTime);

            assertEquals(previousDateTime, result.getPreviousValue());
            assertEquals(newDateTime, result.getNewValue());
        }
    }
}
