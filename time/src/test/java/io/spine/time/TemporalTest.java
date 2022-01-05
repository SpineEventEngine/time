/*
 * Copyright 2022, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import io.spine.base.Time;
import io.spine.time.given.InstantTemporal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.google.common.collect.BoundType.CLOSED;
import static com.google.common.collect.BoundType.OPEN;
import static com.google.common.collect.Range.range;
import static com.google.common.truth.Truth.assertThat;
import static io.spine.testing.Assertions.assertIllegalArgument;
import static io.spine.time.given.ImportantTimes.future;
import static io.spine.time.given.ImportantTimes.inBetween;
import static io.spine.time.given.ImportantTimes.past;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("`Temporal` should")
class TemporalTest {

    @Test
    @DisplayName("not accept `null`s for comparison")
    void nullTest() {
        new NullPointerTester()
                .setDefault(TimestampTemporal.class, future())
                .setDefault(Instant.class, future().toInstant())
                .setDefault(Timestamp.class, future().toTimestamp())
                .testAllPublicInstanceMethods(future());
    }

    @Nested
    @DisplayName("compare to")
    class CompareTo {

        @Test
        @DisplayName("an instance of same type")
        void sameType() {
            var greater = future();
            var lesser = past();
            var comparisonResult = greater.compareTo(lesser);
            assertThat(comparisonResult).isGreaterThan(0);
        }

        @Test
        @DisplayName("failing to compare to a different type of `Temporal`")
        @SuppressWarnings("unchecked") // Supposed to fail.
        void failWithDifferentTypes() {
            var instant = Instant.now();
            @SuppressWarnings("rawtypes")
            Temporal instantTemporal = new InstantTemporal(instant);
            @SuppressWarnings("rawtypes")
            Temporal timestampTemporal = Temporals.from(instant);

            assertIllegalArgument(() -> instantTemporal.compareTo(timestampTemporal));
        }

        @Test
        @DisplayName("other point in time")
        void anInstant() {
            var future = future();
            var past = past();

            assertThat(future.compareTo(past.toInstant()))
                    .isGreaterThan(0);
            assertThat(future.compareTo(past.toTimestamp()))
                    .isGreaterThan(0);

            assertThat(past.compareTo(future.toInstant()))
                    .isLessThan(0);
            assertThat(past.compareTo(future.toTimestamp()))
                    .isLessThan(0);
        }
    }

    @Nested
    @DisplayName("compare two points in time with")
    class EarlierOrLater {

        private final TimestampTemporal later = future();
        private final TimestampTemporal earlier = past();

        @Test
        @DisplayName("`isEarlierThan`")
        void earlierThan() {
            assertFalse(later.isBefore(earlier));
            assertFalse(later.isBefore(earlier.toInstant()));
            assertFalse(later.isBefore(earlier.toTimestamp()));

            assertTrue(earlier.isBefore(later));
            assertTrue(earlier.isBefore(later.toInstant()));
            assertTrue(earlier.isBefore(later.toTimestamp()));
        }

        @Test
        @DisplayName("`isEarlierOrSame`")
        void earlierOrSame() {
            assertFalse(later.isBeforeOrSameAs(earlier));
            assertFalse(later.isBeforeOrSameAs(earlier.toInstant()));
            assertFalse(later.isBeforeOrSameAs(earlier.toTimestamp()));

            assertTrue(earlier.isBeforeOrSameAs(later));
            assertTrue(earlier.isBeforeOrSameAs(later.toInstant()));
            assertTrue(earlier.isBeforeOrSameAs(later.toTimestamp()));

            assertTrue(earlier.isBeforeOrSameAs(earlier));
            assertTrue(earlier.isBeforeOrSameAs(earlier.toInstant()));
            assertTrue(earlier.isBeforeOrSameAs(earlier.toTimestamp()));
        }

        @Test
        @DisplayName("`isSameAs`")
        void sameAs() {
            assertTrue(earlier.isSameAs(earlier));
            assertTrue(earlier.isSameAs(earlier.toInstant()));
            assertTrue(earlier.isSameAs(earlier.toTimestamp()));

            assertFalse(earlier.isSameAs(later));
            assertFalse(earlier.isSameAs(later.toInstant()));
            assertFalse(earlier.isSameAs(later.toTimestamp()));
        }

        @Test
        @DisplayName("`isLaterThan`")
        void laterThan() {
            assertTrue(later.isAfter(earlier));
            assertTrue(later.isAfter(earlier.toInstant()));
            assertTrue(later.isAfter(earlier.toTimestamp()));

            assertTrue(later.isAfterOrSameAs(earlier));
            assertTrue(later.isAfterOrSameAs(earlier.toInstant()));
            assertTrue(later.isAfterOrSameAs(earlier.toTimestamp()));

            assertFalse(earlier.isAfter(later));
            assertFalse(earlier.isAfter(later.toInstant()));
            assertFalse(earlier.isAfter(later.toTimestamp()));

            assertFalse(earlier.isAfterOrSameAs(later));
            assertFalse(earlier.isAfterOrSameAs(later.toInstant()));
            assertFalse(earlier.isAfterOrSameAs(later.toTimestamp()));

            assertTrue(earlier.isAfterOrSameAs(earlier));
            assertTrue(earlier.isAfterOrSameAs(earlier.toInstant()));
            assertTrue(earlier.isAfterOrSameAs(earlier.toTimestamp()));

            assertTrue(earlier.isAfterOrSameAs(earlier));
            assertTrue(earlier.isAfterOrSameAs(earlier.toInstant()));
            assertTrue(earlier.isAfterOrSameAs(earlier.toTimestamp()));
        }
    }

    @Nested
    @DisplayName("see if the point is in the range")
    class InRange {

        @Test
        @DisplayName("of the given bounds")
        void bounds() {
            var past = past();
            var inBetween = inBetween();
            var future = future();

            assertThat(inBetween).isIn(range(past, OPEN, future, CLOSED));

            assertTrue(inBetween.isBetween(past, future));
            assertTrue(inBetween.isBetween(past.toInstant(), future.toInstant()));
            assertTrue(inBetween.isBetween(past.toTimestamp(), future.toTimestamp()));

            assertFalse(past.isBetween(inBetween, future));
            assertFalse(past.isBetween(inBetween.toInstant(), future.toInstant()));
            assertFalse(past.isBetween(inBetween.toTimestamp(), future.toTimestamp()));

            assertFalse(future.isBetween(past, inBetween));
            assertFalse(future.isBetween(past.toInstant(), inBetween.toInstant()));
            assertFalse(future.isBetween(past.toTimestamp(), inBetween.toTimestamp()));

            assertFalse(past.isBetween(past, future));
            assertFalse(past.isBetween(past.toInstant(), future.toInstant()));
            assertFalse(past.isBetween(past.toTimestamp(), future.toTimestamp()));

            assertTrue(future.isBetween(past, future));
            assertTrue(future.isBetween(past.toInstant(), future.toInstant()));
            assertTrue(future.isBetween(past.toTimestamp(), future.toTimestamp()));
        }

        @Test
        @DisplayName("checking that the first bound is lower")
        void correctBounds() {
            var temporal = inBetween();
            assertIllegalArgument(() -> temporal.isBetween(future(), past()));
            assertIllegalArgument(() -> temporal.isBetween(future(), future()));
        }
    }

    @Nested
    @DisplayName("compare to current time and")
    class RelativeTime {

        @BeforeEach
        void setUp() {
            var frozenTime = inBetween().toTimestamp();
            Time.setProvider(() -> frozenTime);
        }

        @AfterEach
        void tearDown() {
            Time.resetProvider();
        }

        @Test
        @DisplayName("tell if in the future")
        void tellFuture() {
            assertTrue(future().isInFuture());
        }

        @Test
        @DisplayName("tell if in the past")
        void tellPast() {
            assertTrue(past().isInPast());
        }
    }
}
