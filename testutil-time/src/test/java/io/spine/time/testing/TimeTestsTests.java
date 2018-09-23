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

package io.spine.time.testing;

import com.google.common.testing.NullPointerTester;
import com.google.protobuf.Duration;
import com.google.protobuf.Timestamp;
import io.spine.base.Time;
import io.spine.testing.UtilityClassTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.protobuf.util.Durations.fromSeconds;
import static com.google.protobuf.util.Timestamps.add;
import static com.google.protobuf.util.Timestamps.subtract;
import static io.spine.base.Time.getCurrentTime;
import static io.spine.testing.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.Durations2.fromMinutes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("TimeTests should")
class TimeTestsTests extends UtilityClassTest<TimeTests> {

    private static final Duration TEN_SECONDS = fromSeconds(10L);
    private static final Duration MINUTE = fromMinutes(1);

    TimeTestsTests() {
        super(TimeTests.class);
    }

    @Override
    protected void setDefaults(NullPointerTester tester) {
        super.setDefaults(tester);
        tester.setDefault(Timestamp.class, getCurrentTime());
    }

    @Test
    @DisplayName("have utility constructors in nested utility classes")
    void haveUtilityCtors() {
        assertHasPrivateParameterlessCtor(TimeTests.Future.class);
        assertHasPrivateParameterlessCtor(TimeTests.Past.class);
    }

    @Test
    @DisplayName("have frozen time provider")
    void frozenTimeProvider() {
        final Timestamp fiveMinutesAgo = subtract(getCurrentTime(),
                                                  fromMinutes(5));

        final Time.Provider provider =
                new TimeTests.FrozenMadHatterParty(fiveMinutesAgo);

        assertEquals(fiveMinutesAgo, provider.getCurrentTime());
    }

    @Test
    @DisplayName("obtain current time in seconds")
    void currentTimeSeconds() {
        assertNotEquals(0, TimeTests.currentTimeSeconds());
    }

    @Test
    @DisplayName("obtain time in the future")
    void obtainFutureTime() {
        Timestamp currentTime = getCurrentTime();
        Timestamp expected = add(currentTime, TEN_SECONDS);

        Timestamp actual = TimeTests.Future.secondsFromNow(TEN_SECONDS.getSeconds());

        assertEquals(expected.getSeconds(), actual.getSeconds());
    }

    @Test
    @DisplayName("obtain time in the past")
    void obtainPastTime() {
        Timestamp currentTime = getCurrentTime();
        Timestamp expected = subtract(currentTime, TEN_SECONDS);

        Timestamp actual = TimeTests.Past.secondsAgo(TEN_SECONDS.getSeconds());

        assertEquals(expected.getSeconds(), actual.getSeconds());
    }

    @Test
    @DisplayName("create instances for minutes ago")
    void minutesAgo() {
        Timestamp currentTime = getCurrentTime();
        Timestamp expected = subtract(currentTime, MINUTE);

        Timestamp actual = TimeTests.Past.minutesAgo(1);

        Assertions.assertEquals(expected.getSeconds(), actual.getSeconds());
    }
}
