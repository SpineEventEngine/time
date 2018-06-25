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
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.google.common.testing.SerializableTester.reserializeAndAssert;
import static io.spine.base.Time.getCurrentTime;
import static io.spine.test.DisplayNames.HAVE_PARAMETERLESS_CTOR;
import static io.spine.test.TestValues.random;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.Constants.HOURS_PER_DAY;
import static io.spine.time.Constants.MINUTES_PER_HOUR;
import static io.spine.time.Constants.SECONDS_PER_MINUTE;
import static io.spine.time.Constants.NANOS_PER_SECOND;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alexander Aleksandrov
 * @author Alexander Yevsyukov
 */
@SuppressWarnings("ClassCanBeStatic")
@DisplayName("OffsetTimes should")
class OffsetTimesTest extends AbstractZonedTimeTest {

    @Override
    protected void assertConversionAt(ZoneOffset zoneOffset) {
        OffsetTime now = OffsetTimes.now(zoneOffset);
        String str = OffsetTimes.toString(now);
        OffsetTime parsed = OffsetTimes.parse(str);
        assertEquals(now, parsed);
    }

    @Test
    @DisplayName(HAVE_PARAMETERLESS_CTOR)
    void haveUtilityConstructor() {
        assertHasPrivateParameterlessCtor(OffsetTimes.class);
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

    @Test
    @DisplayName("reject null values")
    void nullCheck() {
        new NullPointerTester()
                .setDefault(Timestamp.class, getCurrentTime())
                .setDefault(OffsetTime.class, OffsetTimes.now())
                .setDefault(ZoneOffset.class, zoneOffset())
                .setDefault(LocalTime.class, LocalTimes.now())
                .testAllPublicStaticMethods(OffsetTimes.class);
    }

    @Test
    @DisplayName("provide serializable Converter")
    void converter() {
        reserializeAndAssert(OffsetTimes.converter());
    }
}
