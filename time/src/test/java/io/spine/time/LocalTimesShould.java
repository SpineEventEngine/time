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
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;

import static io.spine.base.Time.getCurrentTime;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.SiTime.MILLIS_PER_SECOND;
import static io.spine.time.SiTime.NANOS_PER_MILLISECOND;
import static io.spine.time.Calendars.getHours;
import static io.spine.time.Calendars.getMinutes;
import static io.spine.time.LocalTimes.addHours;
import static io.spine.time.LocalTimes.addMillis;
import static io.spine.time.LocalTimes.addMinutes;
import static io.spine.time.LocalTimes.addSeconds;
import static io.spine.time.LocalTimes.of;
import static io.spine.time.LocalTimes.parse;
import static io.spine.time.LocalTimes.subtractHours;
import static io.spine.time.LocalTimes.subtractMillis;
import static io.spine.time.LocalTimes.subtractMinutes;
import static io.spine.time.LocalTimes.subtractSeconds;
import static java.util.Calendar.getInstance;
import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Aleksandrov
 * @author Alexander Yevsyukov
 */
public class LocalTimesShould {

    private static final int hours = 9;
    private static final int minutes = 25;
    private static final int seconds = 30;
    private static final int nanos = 122;

    /**
     * Local time value for computation tests.
     */
    private LocalTime value;

    @Test
    public void have_utility_constructor() {
        assertHasPrivateParameterlessCtor(LocalTimes.class);
    }

    @Before
    public void setUp() {
        value = of(hours, minutes, seconds, nanos);
    }

    @Test
    public void obtain_current_time() {
        final LocalTime now = LocalTimes.now();
        final Calendar cal = getInstance();

        final int expectedHours = getHours(cal);
        final int expectedMinutes = getMinutes(cal);

        /*
          Assert only hours and minutes to reduce the probability of going over the second boundary
          between the LocalTime and Calendar instances construction in the initialization above.
        */
        assertEquals(expectedHours, now.getHour());
        assertEquals(expectedMinutes, now.getMinute());
    }

    @Test
    public void create_by_hours_and_minutes() {
        final LocalTime localTime = of(hours, minutes);

        assertEquals(hours, localTime.getHour());
        assertEquals(minutes, localTime.getMinute());
    }

    @Test
    public void create_by_hours_minutes_seconds() {
        final LocalTime localTime = of(hours, minutes, seconds);

        assertEquals(hours, localTime.getHour());
        assertEquals(minutes, localTime.getMinute());
        assertEquals(seconds, localTime.getSecond());
    }

    @Test
    public void create_with_nano_precision() {
        final LocalTime localTime = of(hours, minutes, seconds, nanos);

        assertEquals(hours, localTime.getHour());
        assertEquals(minutes, localTime.getMinute());
        assertEquals(seconds, localTime.getSecond());
        assertEquals(nanos, localTime.getNano());
    }

    @Test
    public void add_Hour() {
        final int hoursToAdd = 2;
        final LocalTime localTime = of(hours, minutes, seconds, nanos);
        final LocalTime inFewHours = addHours(localTime, hoursToAdd);

        assertEquals(hours + hoursToAdd, inFewHours.getHour());
        assertEquals(minutes, inFewHours.getMinute());
        assertEquals(seconds, inFewHours.getSecond());
        assertEquals(nanos, inFewHours.getNano());
    }

    @Test
    public void add_minutes() {
        final int minutesToAdd = 15;
        final LocalTime inFewMinutes = addMinutes(value, minutesToAdd);

        assertEquals(hours, inFewMinutes.getHour());
        assertEquals(minutes + minutesToAdd, inFewMinutes.getMinute());
        assertEquals(seconds, inFewMinutes.getSecond());
        assertEquals(nanos, inFewMinutes.getNano());
    }

    @Test
    public void add_seconds() {
        final int secondsToAdd = 18;
        final LocalTime inFewSeconds = addSeconds(value, secondsToAdd);

        assertEquals(hours, inFewSeconds.getHour());
        assertEquals(minutes, inFewSeconds.getMinute());
        assertEquals(seconds + secondsToAdd, inFewSeconds.getSecond());
        assertEquals(nanos, inFewSeconds.getNano());
    }

    @Test
    public void add_millis() {
        final int millisToAdd = 288;
        final LocalTime inFewMillis = addMillis(value, millisToAdd);

        assertEquals(hours, inFewMillis.getHour());
        assertEquals(minutes, inFewMillis.getMinute());
        assertEquals(seconds, inFewMillis.getSecond());
        assertEquals(nanos, inFewMillis.getNano());
    }

    @Test
    public void subtract_Hour() {
        final int hoursToSubtract = 2;
        final LocalTime beforeFewHours = subtractHours(value, hoursToSubtract);

        assertEquals(hours - hoursToSubtract, beforeFewHours.getHour());
        assertEquals(minutes, beforeFewHours.getMinute());
        assertEquals(seconds, beforeFewHours.getSecond());
        assertEquals(nanos, beforeFewHours.getNano());
    }

    @Test
    public void subtract_minutes() {
        final int minutesToSubtract = 15;
        final LocalTime beforeFewMinutes = subtractMinutes(value, minutesToSubtract);

        assertEquals(hours, beforeFewMinutes.getHour());
        assertEquals(minutes - minutesToSubtract, beforeFewMinutes.getMinute());
        assertEquals(seconds, beforeFewMinutes.getSecond());
        assertEquals(nanos, beforeFewMinutes.getNano());
    }

    @Test
    public void subtract_seconds() {
        final int secondsToSubtract = 12;
        final LocalTime beforeFewSeconds = subtractSeconds(value, secondsToSubtract);

        assertEquals(hours, beforeFewSeconds.getHour());
        assertEquals(minutes, beforeFewSeconds.getMinute());
        assertEquals(seconds - secondsToSubtract, beforeFewSeconds.getSecond());
        assertEquals(nanos, beforeFewSeconds.getNano());
    }

    @Test
    public void subtract_millis() {
        final int millisToSubtract = 28;
        final LocalTime beforeFewMillis = subtractMillis(value, millisToSubtract);

        assertEquals(hours, beforeFewMillis.getHour());
        assertEquals(minutes, beforeFewMillis.getMinute());
        assertEquals(seconds, beforeFewMillis.getSecond());
        assertEquals(nanos, beforeFewMillis.getNano());
    }

    //
    // Arguments check
    //-------------------

    @Test
    public void pass_null_tolerance_check() {
        new NullPointerTester()
                .setDefault(Timestamp.class, getCurrentTime())
                .setDefault(ZoneOffset.class, ZoneOffsets.UTC)
                .setDefault(LocalTime.class, LocalTimes.now())
                .testAllPublicStaticMethods(LocalTimes.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_Hour() {
        of(-2, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_oob_Hour() {
        of(24, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_minutes() {
        of(0, -20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_oob_minutes() {
        of(0, 60);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_seconds() {
        of(0, 0, -50);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_oob_seconds() {
        of(0, 0, 60);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_millis() {
        of(0, 0, 0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_oob_millis() {
        of(0, 0, 0, MILLIS_PER_SECOND);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_nanos() {
        of(0, 0, 0, -1501);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_oob_nanos() {
        of(0, 0, 0, NANOS_PER_MILLISECOND);
    }

    //
    // Stringification
    //---------------------

    @Test
    public void convert_to_string_and_back() throws ParseException {
        final LocalTime localTime = of(10, 20, 30, 50);

        final String str = LocalTimes.toString(localTime);
        final LocalTime convertedBack = parse(str);
        assertEquals(localTime, convertedBack);
    }
}
