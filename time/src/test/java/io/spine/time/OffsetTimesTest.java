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

import com.google.common.testing.NullPointerTester;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static io.spine.base.Time.currentTime;
import static io.spine.testing.TestValues.random;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("OffsetTimes should")
class OffsetTimesTest extends AbstractOffsetTimeTest<OffsetTime, java.time.OffsetTime> {

    /** The count of nanoseconds in one second. */
    @SuppressWarnings("NumericCastThatLosesPrecision") // Known to fit.
    private static final int NANOS_PER_SECOND = (int) TimeUnit.SECONDS.toNanos(1);
    /** The count of seconds in one minute. */
    @SuppressWarnings("NumericCastThatLosesPrecision") // Known to fit.
    private static final int SECONDS_PER_MINUTE = (int) TimeUnit.MINUTES.toSeconds(1);
    /** The count of minutes in one hour. */
    @SuppressWarnings("NumericCastThatLosesPrecision") // Known to fit.
    private static final int MINUTES_PER_HOUR = (int) TimeUnit.HOURS.toMinutes(1);
    /** The count of hours per day. */
    @SuppressWarnings("NumericCastThatLosesPrecision") // Known to fit.
    private static final int HOURS_PER_DAY = (int) TimeUnit.DAYS.toHours(1);

    OffsetTimesTest() {
        super(OffsetTimes.class,
              OffsetTimes::now,
              OffsetTimes::toString,
              OffsetTimes::parse,
              OffsetTimes.converter());
    }

    @Override
    protected void assertConversionAt(ZoneOffset zoneOffset) {
        OffsetTime now = OffsetTimes.now(zoneOffset);
        String str = OffsetTimes.toString(now);
        OffsetTime parsed = OffsetTimes.parse(str);
        assertEquals(now, parsed);
    }

    @Override
    void addDefaults(NullPointerTester nullTester) {
        nullTester.setDefault(Timestamp.class, currentTime())
                  .setDefault(OffsetTime.class, OffsetTimes.now())
                  .setDefault(ZoneOffset.class, zoneOffset())
                  .setDefault(LocalTime.class, LocalTimes.now());
    }

    @Nested
    @DisplayName("Create values with")
    class Create {

        @Test
        @DisplayName("current time at offset")
        void nowAtOffset() {
            OffsetTime now = OffsetTimes.now(zoneOffset());
            assertEquals(zoneOffset(), now.getOffset());
        }
        @Test
        @DisplayName("local time at offset")
        void localTimeAtOffset() {
            LocalTime localTime = generateLocalTime();
            OffsetTime distantTime = OffsetTimes.of(localTime, zoneOffset());

            assertEquals(localTime, distantTime.getTime());
            assertEquals(zoneOffset(), distantTime.getOffset());
        }

    }

    private static LocalTime generateLocalTime() {
        int hours = random(HOURS_PER_DAY);
        int minutes = random(MINUTES_PER_HOUR);
        int seconds = random(SECONDS_PER_MINUTE);
        int nanos = random(NANOS_PER_SECOND);
        return LocalTimes.of(hours, minutes, seconds, nanos);
    }
}
