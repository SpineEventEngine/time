/*
 * Copyright 2025, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.protobuf.util.Durations.fromMinutes;
import static com.google.protobuf.util.Durations.fromSeconds;
import static com.google.protobuf.util.Timestamps.add;
import static com.google.protobuf.util.Timestamps.subtract;
import static io.spine.base.Time.currentTime;

@DisplayName("`TimeTests` should")
class TimeTestsTests extends UtilityClassTest<TimeTests> {

    private static final Duration TEN_SECONDS = fromSeconds(10L);
    private static final Duration MINUTE = fromMinutes(1);

    TimeTestsTests() {
        super(TimeTests.class);
    }

    @Override
    protected void configure(NullPointerTester tester) {
        super.configure(tester);
        tester.setDefault(Timestamp.class, currentTime());
    }

    @Test
    @DisplayName("have frozen time provider")
    void frozenTimeProvider() {
        var fiveMinutesAgo = subtract(currentTime(), fromMinutes(5));

        Time.Provider provider = new FrozenMadHatterParty(fiveMinutesAgo);

        assertThat(fiveMinutesAgo)
                .isEqualTo(provider.currentTime());
    }

    @Test
    @DisplayName("obtain current time in seconds")
    void currentTimeSeconds() {
        assertThat(TimeTests.currentTimeSeconds())
                .isNotEqualTo(0);
    }

    @Nested
    @DisplayName("test nested `Future` utility")
    class FutureTest extends UtilityClassTest<Future> {

        FutureTest() {
            super(Future.class);
        }

        @Test
        @DisplayName("and obtain time in the future")
        void obtainFutureTime() {
            var currentTime = currentTime();
            var expected = add(currentTime, TEN_SECONDS);

            var actual = Future.secondsFromNow(TEN_SECONDS.getSeconds());

            assertThat(actual.getSeconds())
                    .isEqualTo(expected.getSeconds());
        }
    }

    @Nested
    @DisplayName("test nested `Past` utility and")
    class PastTest extends UtilityClassTest<Past> {

        PastTest() {
            super(Past.class);
        }

        @Test
        @DisplayName("obtain time in the past")
        void obtainPastTime() {
            var currentTime = currentTime();
            var expected = subtract(currentTime, TEN_SECONDS);

            var actual = Past.secondsAgo(TEN_SECONDS.getSeconds());

            assertThat(actual.getSeconds())
                    .isEqualTo(expected.getSeconds());
        }

        @Test
        @DisplayName("create instances for minutes ago")
        void minutesAgo() {
            var currentTime = currentTime();
            var expected = subtract(currentTime, MINUTE);

            var actual = Past.minutesAgo(1);

            assertThat(actual.getSeconds())
                    .isEqualTo(expected.getSeconds());
        }
    }
}
