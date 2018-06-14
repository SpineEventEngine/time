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
import org.junit.jupiter.api.Test;

import static io.spine.test.DisplayNames.HAVE_PARAMETERLESS_CTOR;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.Intervals.between;
import static io.spine.time.Intervals.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alexander Litus
 */
@SuppressWarnings("ClassCanBeStatic")
class IntervalsTest {

    @Test
    @DisplayName(HAVE_PARAMETERLESS_CTOR)
    void utilityCtor() {
        assertHasPrivateParameterlessCtor(Intervals.class);
    }
    
    @Test
    @DisplayName("verify if an Interval contains a point in time")
    void containsTimestamp() {
        Timestamp start = newTimestamp(5, 6);
        Timestamp end = newTimestamp(10, 10);
        Timestamp between = newTimestamp(7, 100);
        Interval interval = between(start, end);
        assertTrue(contains(interval, between));
    }

    @Test
    @DisplayName("create instance by two Timestamps")
    void creation() {
        Timestamp start = newTimestamp(5, 6);
        Timestamp end = newTimestamp(10, 10);
        Interval interval = between(start, end);

        assertEquals(start, interval.getStart());
        assertEquals(end, interval.getEnd());
    }

    @Test
    @DisplayName("convert Interval to Duration")
    void convertToDuration() {
        Timestamp start = newTimestamp(5, 6);
        Timestamp end = newTimestamp(10, 10);
        Duration expectedDuration = Duration
                .newBuilder()
                .setSeconds(end.getSeconds() - start.getSeconds())
                .setNanos(end.getNanos() - start.getNanos())
                .build();
        Interval interval = between(start, end);
        Duration actualDuration = Intervals.toDuration(interval);

        assertEquals(expectedDuration, actualDuration);
    }

    @Test
    @DisplayName("Reject zero Interval")
    void rejectZeroInterval() {
        Timestamp start = newTimestamp(5, 6);
        Timestamp end = newTimestamp(5, 6);

        assertThrows(
                IllegalArgumentException.class,
                () -> between(start, end)
        );
    }

    private static Timestamp newTimestamp(long seconds, int nanos) {
        return Timestamp.newBuilder()
                        .setSeconds(seconds)
                        .setNanos(nanos)
                        .build();
    }
}
