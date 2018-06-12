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
import io.spine.time.testing.TimeTests;
import org.junit.Test;

import java.util.Date;

import static com.google.protobuf.util.Durations.fromSeconds;
import static com.google.protobuf.util.Timestamps.add;
import static com.google.protobuf.util.Timestamps.subtract;
import static com.google.protobuf.util.Timestamps.toNanos;
import static io.spine.base.Time.getCurrentTime;
import static io.spine.base.Time.resetProvider;
import static io.spine.base.Time.setProvider;
import static io.spine.base.Time.systemTime;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.SiTime.MICROS_PER_SECOND;
import static io.spine.time.SiTime.MILLIS_PER_SECOND;
import static io.spine.time.SiTime.NANOS_PER_MICROSECOND;
import static io.spine.time.SiTime.NANOS_PER_SECOND;
import static io.spine.time.Durations2.fromMinutes;
import static io.spine.time.EarthTime.HOURS_PER_DAY;
import static io.spine.time.EarthTime.SECONDS_PER_HOUR;
import static io.spine.time.Timestamps2.compare;
import static io.spine.time.Timestamps2.isBetween;
import static io.spine.time.Timestamps2.isLaterThan;
import static io.spine.time.Timestamps2.toDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ClassCanBeStatic")
public class Timestamps2Should {

    private static final Duration TEN_SECONDS = fromSeconds(10L);

    private static final Duration MINUTE = fromMinutes(1);

    @Test
    public void have_private_constructor() {
        assertHasPrivateParameterlessCtor(Timestamps2.class);
    }

    @Test
    public void declare_unit_constants() {
        // Make these useful constant used from our library code to prevent
        // accidental removal.
        assertNotEquals(0, NANOS_PER_MICROSECOND);
        assertNotEquals(0, MICROS_PER_SECOND);
        assertNotEquals(0, SECONDS_PER_HOUR);
        assertNotEquals(0, HOURS_PER_DAY);
    }

    @Test
    public void calculate_timestamp_of_moment_minute_ago() {
        Timestamp currentTime = getCurrentTime();
        Timestamp expected = subtract(currentTime, MINUTE);

        Timestamp actual = TimeTests.Past.minutesAgo(1);

        assertEquals(expected.getSeconds(), actual.getSeconds());
    }

    @Test
    public void calculate_timestamp_of_moment_seconds_ago() {
        Timestamp currentTime = getCurrentTime();
        Timestamp expected = subtract(currentTime, TEN_SECONDS);

        Timestamp actual = TimeTests.Past.secondsAgo(TEN_SECONDS.getSeconds());

        assertEquals(expected.getSeconds(), actual.getSeconds());
    }

    @Test
    public void calculate_timestamp_of_moment_in_the_future() {
        Timestamp currentTime = getCurrentTime();
        Timestamp expected = add(currentTime, TEN_SECONDS);

        Timestamp actual = TimeTests.Future.secondsFromNow(TEN_SECONDS.getSeconds());

        assertEquals(expected.getSeconds(), actual.getSeconds());
    }

    @Test
    public void compare_two_timestamps_return_negative_int_if_first_less_than_second_one() {
        Timestamp time1 = getCurrentTime();
        Timestamp time2 = add(time1, TEN_SECONDS);

        int result = compare(time1, time2);

        assertTrue(result < 0);
    }

    @Test
    public void compare_two_timestamps_return_negative_int_if_first_is_null() {
        Timestamp currentTime = getCurrentTime();

        int result = compare(null, currentTime);

        assertTrue(result < 0);
    }

    @Test
    public void compare_two_timestamps_return_zero_if_timestamps_are_equal() {
        int secs = 256;
        int nanos = 512;
        Timestamp time1 = Timestamp.newBuilder()
                                   .setSeconds(secs)
                                   .setNanos(nanos)
                                   .build();
        Timestamp time2 = Timestamp.newBuilder()
                                   .setSeconds(secs)
                                   .setNanos(nanos)
                                   .build();

        int result = compare(time1, time2);

        assertEquals(0, result);
    }

    @Test
    public void compare_two_timestamps_return_zero_if_pass_null() {
        int result = compare(null, null);

        assertEquals(0, result);
    }

    @Test
    public void compare_two_timestamps_return_positive_int_if_first_greater_than_second_one() {
        Timestamp currentTime = getCurrentTime();
        Timestamp timeAfterCurrent = add(currentTime, TEN_SECONDS);

        int result = compare(timeAfterCurrent, currentTime);

        assertTrue(result > 0);
    }

    @Test
    public void compare_two_timestamps_return_positive_int_if_second_one_is_null() {
        Timestamp currentTime = getCurrentTime();

        int result = compare(currentTime, null);

        assertTrue(result > 0);
    }

    @Test
    public void return_true_if_timestamp_is_between_two_timestamps() {
        Timestamp start = getCurrentTime();
        Timestamp timeBetween = add(start, TEN_SECONDS);
        Timestamp finish = add(timeBetween, TEN_SECONDS);

        boolean isBetween = isBetween(timeBetween, start, finish);

        assertTrue(isBetween);
    }

    @Test
    public void return_false_if_timestamp_is_not_between_two_timestamps() {
        Timestamp start = getCurrentTime();
        Timestamp finish = add(start, TEN_SECONDS);
        Timestamp timeNotBetween = add(finish, TEN_SECONDS);

        boolean isBetween = isBetween(timeNotBetween, start, finish);

        assertFalse(isBetween);
    }

    @Test
    public void return_true_if_timestamp_is_after_another_one() {
        Timestamp fromPoint = getCurrentTime();
        Timestamp timeToCheck = add(fromPoint, TEN_SECONDS);

        boolean isAfter = isLaterThan(timeToCheck, fromPoint);

        assertTrue(isAfter);
    }

    @Test
    public void return_false_if_timestamp_is_not_after_another_one() {
        Timestamp fromPoint = getCurrentTime();
        Timestamp timeToCheck = subtract(fromPoint, TEN_SECONDS);

        boolean isAfter = isLaterThan(timeToCheck, fromPoint);

        assertFalse(isAfter);
    }

    @Test
    public void compare_timestamps_return_negative_int_if_first_less_than_second_one() {
        Timestamp time1 = getCurrentTime();
        Timestamp time2 = add(time1, TEN_SECONDS);

        int result = Timestamps.comparator()
                               .compare(time1, time2);

        assertTrue(result < 0);
    }

    @Test
    public void compare_two_timestamps_using_comparator_return_zero_if_timestamps_are_equal() {
        int secs = 256;
        int nanos = 512;
        Timestamp time1 = Timestamp.newBuilder()
                                   .setSeconds(secs)
                                   .setNanos(nanos)
                                   .build();
        Timestamp time2 = Timestamp.newBuilder()
                                   .setSeconds(secs)
                                   .setNanos(nanos)
                                   .build();

        int result = Timestamps.comparator()
                               .compare(time1, time2);

        assertEquals(0, result);
    }

    @Test
    public void compare_timestamps_return_positive_int_if_first_greater_than_second_one() {
        Timestamp currentTime = getCurrentTime();
        Timestamp timeAfterCurrent = add(currentTime, TEN_SECONDS);

        int result = Timestamps.comparator()
                               .compare(timeAfterCurrent, currentTime);

        assertTrue(result > 0);
    }

    @Test
    public void convert_timestamp_to_date_to_nearest_second() {

        Timestamp expectedTime = getCurrentTime();

        Date actualDate = toDate(expectedTime);
        long actualSeconds = actualDate.getTime() / MILLIS_PER_SECOND;

        assertEquals(expectedTime.getSeconds(), actualSeconds);
    }

    @Test
    public void convert_timestamp_to_nanos() {
        Timestamp expectedTime = getCurrentTime();

        long nanos = toNanos(expectedTime);
        long expectedNanos = expectedTime.getSeconds() * NANOS_PER_SECOND +
                expectedTime.getNanos();

        assertEquals(expectedNanos, nanos);
    }

    @Test
    public void accept_time_provider() {
        Timestamp fiveMinutesAgo = subtract(getCurrentTime(),
                                            fromMinutes(5));

        setProvider(new TimeTests.FrozenMadHatterParty(fiveMinutesAgo));

        assertEquals(fiveMinutesAgo, getCurrentTime());
    }

    @Test
    public void reset_time_provider_to_default() {
        Timestamp aMinuteAgo = subtract(
                systemTime(),
                fromMinutes(1));

        setProvider(new TimeTests.FrozenMadHatterParty(aMinuteAgo));
        resetProvider();

        assertNotEquals(aMinuteAgo, getCurrentTime());
    }

    @Test
    public void obtain_system_time_millis() {
        assertNotEquals(0, systemTime());
    }
}
