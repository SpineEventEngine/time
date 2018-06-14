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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.google.protobuf.util.Durations.fromSeconds;
import static com.google.protobuf.util.Timestamps.add;
import static com.google.protobuf.util.Timestamps.subtract;
import static io.spine.base.Time.getCurrentTime;
import static io.spine.test.DisplayNames.HAVE_PARAMETERLESS_CTOR;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.Timestamps2.isLaterThan;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Nested
    @DisplayName("Check that a Timestamp is")
    class Between {

        @Test
        @DisplayName("between two others")
        void isBetween() {
            Timestamp start = getCurrentTime();
            Timestamp timeBetween = add(start, TEN_SECONDS);
            Timestamp finish = add(timeBetween, TEN_SECONDS);

            boolean isBetween = Timestamps2.isBetween(timeBetween, start, finish);

            assertTrue(isBetween);
        }

        @Test
        @DisplayName("not between two others")
        void isNotBetween() {
            Timestamp start = getCurrentTime();
            Timestamp finish = add(start, TEN_SECONDS);
            Timestamp timeNotBetween = add(finish, TEN_SECONDS);

            boolean isBetween = Timestamps2.isBetween(timeNotBetween, start, finish);

            assertFalse(isBetween);
        }
    }

    @Nested
    @DisplayName("Verify that a Timestamp is")
    class Later {

        @Test
        @DisplayName("later than another")
        void isLater() {
            Timestamp fromPoint = getCurrentTime();
            Timestamp timeToCheck = add(fromPoint, TEN_SECONDS);

            boolean isAfter = isLaterThan(timeToCheck, fromPoint);

            assertTrue(isAfter);
        }

        @Test
        @DisplayName("not later than another")
        void isNotLater() {
            Timestamp fromPoint = getCurrentTime();
            Timestamp timeToCheck = subtract(fromPoint, TEN_SECONDS);

            boolean isAfter = isLaterThan(timeToCheck, fromPoint);

            assertFalse(isAfter);
        }
    }
}
