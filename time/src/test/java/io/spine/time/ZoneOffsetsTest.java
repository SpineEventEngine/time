/*
 * Copyright 2023, TeamDev. All rights reserved.
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
import com.google.protobuf.Duration;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static io.spine.base.Time.currentTime;
import static io.spine.protobuf.Durations2.hours;
import static io.spine.protobuf.Durations2.hoursAndMinutes;
import static io.spine.time.ZoneOffsets.ofHours;
import static io.spine.time.ZoneOffsets.ofHoursMinutes;
import static io.spine.time.ZoneOffsets.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ZoneOffsets should")
class ZoneOffsetsTest extends AbstractDateTimeUtilityTest<ZoneOffset, java.time.ZoneOffset> {

    /** The count of milliseconds in one second. */
    @SuppressWarnings("NumericCastThatLosesPrecision") // Known to fit.
    private static final int MILLIS_PER_SECOND = (int) TimeUnit.SECONDS.toMillis(1);

    ZoneOffsetsTest() {
        super(ZoneOffsets.class,
              now -> ZoneOffsets.getDefault(),
              ZoneOffsets::toString,
              ZoneOffsets::parse,
              ZoneOffsets.converter());
    }

    @Override
    void addDefaults(NullPointerTester nullTester) {
        // None.
    }

    @Nested
    @DisplayName("Create an instance")
    class Create {

        @Test
        @DisplayName("for the current time zone")
        void currentTimeZone() {
            TimeZone timeZone = TimeZone.getDefault();
            ZoneOffset zoneOffset = ZoneOffsets.getDefault();

            Timestamp now = currentTime();
            long date = Timestamps.toMillis(now);
            int offsetSeconds = timeZone.getOffset(date) / MILLIS_PER_SECOND;

            assertEquals(offsetSeconds, zoneOffset.getAmountSeconds());
        }

        @Test
        @DisplayName("by hour offset")
        void byHourOffset() {
            Duration twoHours = hours(2);
            assertEquals(twoHours.getSeconds(), ofHours(2).getAmountSeconds());
        }

        @Test
        @DisplayName("by a positive offset of hours and minutes")
        void byHoursAndMinutes() {
            assertEquals(hoursAndMinutes(8, 45).getSeconds(),
                         ofHoursMinutes(8, 45).getAmountSeconds());
        }

        @Test
        @DisplayName("by a negative offset of hours and minutes")
        void byNegativeHoursAndMinutes() {
            assertEquals(hoursAndMinutes(-4, -50).getSeconds(),
                         ofHoursMinutes(-4, -50).getAmountSeconds());
        }
    }

    @Test
    @DisplayName("return UTC for zero offset")
    void zeroOffset() {
        assertSame(ZoneOffsets.utc(), ZoneOffsets.ofSeconds(0));
    }

    @Nested
    @DisplayName("Do not accept")
    class Arguments {

        @Test
        @DisplayName("negative hour with positive minutes")
        void negativeHourPositiveMinutes() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> ofHoursMinutes(-1, 10)
            );
        }

        @Test
        @DisplayName("positive hour and negative minutes")
        void positiveHourNegativeMinutes() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> ofHoursMinutes(1, -10)
            );
        }

        @Test
        @DisplayName("more than 18 hours")
        void over18Hours() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> ofHours(18 + 1)
            );
        }

        @Test
        @DisplayName("less than -18 hours")
        void overMinus18Hours() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> ofHours(-18 - 1)
            );
        }

        @Test
        @DisplayName("more than 60 minutes")
        void over60Minutes() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> ofHoursMinutes(10, 60 + 1)
            );
        }

    }

    @Nested
    @DisplayName("Convert to String")
    class Stringify {

        @Test
        @DisplayName("positive offset")
        void positive() {
            ZoneOffset positive = ofHoursMinutes(5, 48);
            assertEquals(positive, parse(ZoneOffsets.toString(positive)));
        }

        @Test
        @DisplayName("negative offset")
        void negative() {
            ZoneOffset negative = ofHoursMinutes(-3, -36);
            assertEquals(negative, parse(ZoneOffsets.toString(negative)));
        }
    }

    @Nested
    @DisplayName("Parse offset formats")
    class Parse {

        @Test
        @DisplayName("+hhmm")
        void noSeparator() {
            assertEquals(ofHoursMinutes(4, 30), parse("+0430"));
        }

        @Test
        @DisplayName("+hh:mm")
        void withSeparator() {
            assertEquals(ofHoursMinutes(4, 30), parse("+04:30"));
        }

        @Test
        @DisplayName("-hhmm")
        void negativeNoSeparator() {
            assertEquals(ofHoursMinutes(-2, -45), parse("-0245"));
        }

        @Test
        @DisplayName("-hh:mm")
        void negativeWithSeparator() {
            assertEquals(ofHoursMinutes(-2, -45), parse("-02:45"));
        }

        @Test
        @DisplayName("and fail when sign char missing")
        void signCharMissing() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> parse("x03:00")
            );
        }
    }
}
