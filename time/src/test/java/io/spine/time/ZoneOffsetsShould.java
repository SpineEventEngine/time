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

import com.google.protobuf.Duration;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import org.junit.Test;

import java.text.ParseException;
import java.time.Instant;
import java.util.TimeZone;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.base.Time.getCurrentTime;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.SiTime.MILLIS_PER_SECOND;
import static io.spine.time.Durations2.hours;
import static io.spine.time.Durations2.hoursAndMinutes;
import static io.spine.time.ZoneOffsets.adjustZero;
import static io.spine.time.ZoneOffsets.ofHours;
import static io.spine.time.ZoneOffsets.ofHoursMinutes;
import static io.spine.time.ZoneOffsets.parse;
import static org.junit.Assert.assertEquals;

public class ZoneOffsetsShould {

    @Test
    public void has_private_constructor() {
        assertHasPrivateParameterlessCtor(ZoneOffsets.class);
    }

    @Test
    public void get_current_zone_offset() {
        TimeZone timeZone = TimeZone.getDefault();
        ZoneOffset zoneOffset = ZoneOffsets.getDefault();

        Timestamp now = getCurrentTime();
        long date = Timestamps.toMillis(now);
        int offsetSeconds = timeZone.getOffset(date) / MILLIS_PER_SECOND;

        assertEquals(offsetSeconds, zoneOffset.getAmountSeconds());
    }

    @Test
    public void create_instance_by_hours_offset() {
        Duration twoHours = hours(2);
        assertEquals(twoHours.getSeconds(), ofHours(2).getAmountSeconds());
    }

    @Test
    public void create_instance_by_hours_and_minutes_offset() {
        assertEquals(hoursAndMinutes(8, 45).getSeconds(),
                     ofHoursMinutes(8, 45).getAmountSeconds());

        assertEquals(hoursAndMinutes(-4, -50).getSeconds(),
                     ofHoursMinutes(-4, -50).getAmountSeconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void require_same_sign_for_hours_and_minutes_negative_Hour() {
        ofHoursMinutes(-1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void require_same_sign_for_hours_and_minutes_positive_Hour() {
        ofHoursMinutes(1, -10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void do_not_accept_more_than_18_Hour() {
        ofHours(18 + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void do_not_accept_more_than_18_hours_by_abs() {
        ofHours(-18 - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void do_not_accept_more_than_60_minutes() {
        ofHoursMinutes(10, 60 + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void do_not_accept_more_than_17_hours_and_60_minutes() {
        ofHoursMinutes(3, 0 - 1);
    }

    @Test
    public void convert_to_string() throws ParseException {
        ZoneOffset positive = ofHoursMinutes(5, 48);
        ZoneOffset negative = ofHoursMinutes(-3, -36);

        assertEquals(positive, parse(ZoneOffsets.toString(positive)));
        assertEquals(negative, parse(ZoneOffsets.toString(negative)));
    }

    @Test
    public void parse_string() {
        assertEquals(ofHoursMinutes(4, 30), parse("+0430"));
        assertEquals(ofHoursMinutes(4, 30), parse("+04:30"));

        assertEquals(ofHoursMinutes(-2, -45), parse("-0245"));
        assertEquals(ofHoursMinutes(-2, -45), parse("-02:45"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_when_sign_char_missing() {
        parse("x03:00");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_when_hours_and_minutes_have_different_sign_negative_Hour() {
        ofHoursMinutes(-1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_when_hours_and_minutes_have_different_sign_negative_minutes() {
        ofHoursMinutes(1, -10);
    }

    @Test
    public void adjust_zero_offset_without_zone() {
        assertEquals(ZoneOffsets.UTC, adjustZero(ZoneOffsets.ofSeconds(0)));

        ZoneOffset offset = ZoneOffsets.getDefault();
        assertEquals(offset, adjustZero(offset));

        ZoneOffset gmtOffset = ZoneOffsets.of(java.time.ZoneId.of("GMT")
                                                              .getRules()
                                                              .getOffset(Instant.now()));
        checkNotNull(gmtOffset);
        assertEquals(gmtOffset, adjustZero(gmtOffset));
    }
}
