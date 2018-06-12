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
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Calendar;

import static io.spine.base.Time.getCurrentTime;
import static io.spine.test.DisplayNames.HAVE_PARAMETERLESS_CTOR;
import static io.spine.test.TestValues.random;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.Calendars.at;
import static io.spine.time.Calendars.getHours;
import static io.spine.time.Calendars.getMinutes;
import static io.spine.time.Calendars.getSeconds;
import static io.spine.time.Calendars.getZoneOffset;
import static io.spine.time.EarthTime.HOURS_PER_DAY;
import static io.spine.time.EarthTime.MINUTES_PER_HOUR;
import static io.spine.time.EarthTime.SECONDS_PER_MINUTE;
import static io.spine.time.SiTime.NANOS_PER_SECOND;
import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Aleksandrov
 * @author Alexander Yevsyukov
 */
public class OffsetTimesShould extends AbstractZonedTimeTest {

    @Test
    @DisplayName(HAVE_PARAMETERLESS_CTOR)
    void haveUtilityConstructor() {
        assertHasPrivateParameterlessCtor(OffsetTimes.class);
    }

    @Test
    void obtain_current() {
        final OffsetTime now = OffsetTimes.now(zoneOffset);
        final Calendar cal = at(zoneOffset);

        final LocalTime time = now.getTime();
        assertEquals(getHours(cal), time.getHour());
        assertEquals(getMinutes(cal), time.getMinute());
        assertEquals(getSeconds(cal), time.getSecond());
        assertEquals(getZoneOffset(cal), now.getOffset().getAmountSeconds());
        /* We cannot check milliseconds and nanos due to time gap between object creation */
    }

    @Test
    public void create_instance_on_local_time_at_offset() {
        LocalTime localTime = generateLocalTime();
        OffsetTime delhiTime = OffsetTimes.of(localTime, zoneOffset);

        assertEquals(localTime, delhiTime.getTime());
        assertEquals(zoneOffset, delhiTime.getOffset());
    }

    private static LocalTime generateLocalTime() {
        int hours = random(HOURS_PER_DAY);
        int minutes = random(MINUTES_PER_HOUR);
        int seconds = random(SECONDS_PER_MINUTE);
        int nanos = random(NANOS_PER_SECOND);
        return LocalTimes.of(hours, minutes, seconds, nanos);
    }


    @Test
    public void pass_null_tolerance_test() {
        new NullPointerTester()
                .setDefault(Timestamp.class, getCurrentTime())
                .setDefault(OffsetTime.class, OffsetTimes.now(zoneOffset))
                .setDefault(ZoneOffset.class, zoneOffset)
                .setDefault(LocalTime.class, LocalTimes.now())
                .testAllPublicStaticMethods(OffsetTimes.class);
    }

    @Override
    protected void assertConversionAt(ZoneOffset zoneOffset) {
        OffsetTime now = OffsetTimes.now(zoneOffset);
        String str = OffsetTimes.toString(now);
        OffsetTime parsed = OffsetTimes.parse(str);
        assertEquals(now, parsed);
    }
}
