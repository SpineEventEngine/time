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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static com.google.protobuf.util.Durations.fromSeconds;
import static com.google.protobuf.util.Timestamps.add;
import static com.google.protobuf.util.Timestamps.subtract;
import static com.google.protobuf.util.Timestamps.toNanos;
import static io.spine.base.Time.getCurrentTime;
import static io.spine.base.Time.resetProvider;
import static io.spine.base.Time.setProvider;
import static io.spine.base.Time.systemTime;
import static io.spine.test.DisplayNames.HAVE_PARAMETERLESS_CTOR;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.Durations2.fromMinutes;
import static io.spine.time.SiTime.MILLIS_PER_SECOND;
import static io.spine.time.SiTime.NANOS_PER_SECOND;
import static io.spine.time.Timestamps2.compare;
import static io.spine.time.Timestamps2.isBetween;
import static io.spine.time.Timestamps2.isLaterThan;
import static io.spine.time.Timestamps2.toDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("ClassCanBeStatic")
@DisplayName("Timestamps2 should")
class Timestamps2Test {

    private static final Duration TEN_SECONDS = fromSeconds(10L);


    @Test
    @DisplayName(HAVE_PARAMETERLESS_CTOR)
    void utilityCtor() {
        assertHasPrivateParameterlessCtor(Timestamps2.class);
    }


    @Test
    void compare_two_timestamps_return_negative_int_if_first_less_than_second_one() {
        Timestamp time1 = getCurrentTime();
        Timestamp time2 = add(time1, TEN_SECONDS);

        int result = compare(time1, time2);

        assertTrue(result < 0);
    }

    @Test
    void compare_two_timestamps_return_negative_int_if_first_is_null() {
        Timestamp currentTime = getCurrentTime();

        int result = compare(null, currentTime);

        assertTrue(result < 0);
    }

    @Test
    void compare_two_timestamps_return_zero_if_timestamps_are_equal() {
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
    void compare_two_timestamps_return_zero_if_pass_null() {
        int result = compare(null, null);

        assertEquals(0, result);
    }

    @Test
    void compare_two_timestamps_return_positive_int_if_first_greater_than_second_one() {
        Timestamp currentTime = getCurrentTime();
        Timestamp timeAfterCurrent = add(currentTime, TEN_SECONDS);

        int result = compare(timeAfterCurrent, currentTime);

        assertTrue(result > 0);
    }

    @Test
    void compare_two_timestamps_return_positive_int_if_second_one_is_null() {
        Timestamp currentTime = getCurrentTime();

        int result = compare(currentTime, null);

        assertTrue(result > 0);
    }

    @Test
    void return_true_if_timestamp_is_between_two_timestamps() {
        Timestamp start = getCurrentTime();
        Timestamp timeBetween = add(start, TEN_SECONDS);
        Timestamp finish = add(timeBetween, TEN_SECONDS);

        boolean isBetween = isBetween(timeBetween, start, finish);

        assertTrue(isBetween);
    }

    @Test
    void return_false_if_timestamp_is_not_between_two_timestamps() {
        Timestamp start = getCurrentTime();
        Timestamp finish = add(start, TEN_SECONDS);
        Timestamp timeNotBetween = add(finish, TEN_SECONDS);

        boolean isBetween = isBetween(timeNotBetween, start, finish);

        assertFalse(isBetween);
    }

    @Test
    void return_true_if_timestamp_is_after_another_one() {
        Timestamp fromPoint = getCurrentTime();
        Timestamp timeToCheck = add(fromPoint, TEN_SECONDS);

        boolean isAfter = isLaterThan(timeToCheck, fromPoint);

        assertTrue(isAfter);
    }

    @Test
    void return_false_if_timestamp_is_not_after_another_one() {
        Timestamp fromPoint = getCurrentTime();
        Timestamp timeToCheck = subtract(fromPoint, TEN_SECONDS);

        boolean isAfter = isLaterThan(timeToCheck, fromPoint);

        assertFalse(isAfter);
    }

    @Test
    void compare_timestamps_return_negative_int_if_first_less_than_second_one() {
        Timestamp time1 = getCurrentTime();
        Timestamp time2 = add(time1, TEN_SECONDS);

        int result = Timestamps.comparator()
                               .compare(time1, time2);

        assertTrue(result < 0);
    }

    @Test
    void compare_two_timestamps_using_comparator_return_zero_if_timestamps_are_equal() {
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
    void compare_timestamps_return_positive_int_if_first_greater_than_second_one() {
        Timestamp currentTime = getCurrentTime();
        Timestamp timeAfterCurrent = add(currentTime, TEN_SECONDS);

        int result = Timestamps.comparator()
                               .compare(timeAfterCurrent, currentTime);

        assertTrue(result > 0);
    }

    @Test
    void convert_timestamp_to_date_to_nearest_second() {

        Timestamp expectedTime = getCurrentTime();

        Date actualDate = toDate(expectedTime);
        long actualSeconds = actualDate.getTime() / MILLIS_PER_SECOND;

        assertEquals(expectedTime.getSeconds(), actualSeconds);
    }

    @Test
    void convert_timestamp_to_nanos() {
        Timestamp expectedTime = getCurrentTime();

        long nanos = toNanos(expectedTime);
        long expectedNanos = expectedTime.getSeconds() * NANOS_PER_SECOND +
                expectedTime.getNanos();

        assertEquals(expectedNanos, nanos);
    }

    @Test
    void accept_time_provider() {
        Timestamp fiveMinutesAgo = subtract(getCurrentTime(),
                                            fromMinutes(5));

        setProvider(new TimeTests.FrozenMadHatterParty(fiveMinutesAgo));

        assertEquals(fiveMinutesAgo, getCurrentTime());
    }

    @Test
    void reset_time_provider_to_default() {
        Timestamp aMinuteAgo = subtract(
                systemTime(),
                fromMinutes(1));

        setProvider(new TimeTests.FrozenMadHatterParty(aMinuteAgo));
        resetProvider();

        assertNotEquals(aMinuteAgo, getCurrentTime());
    }

    @Test
    void obtain_system_time_millis() {
        assertNotEquals(0, systemTime());
    }
}
