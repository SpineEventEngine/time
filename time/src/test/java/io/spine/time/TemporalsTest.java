/*
 * Copyright 2023, TeamDev. All rights reserved.
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

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.spine.base.Time;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static io.spine.time.given.TimestampTemporalTestEnv.assertEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("`Temporal` factory should")
class TemporalsTest {

    @Test
    @DisplayName("create a `Temporal` from a `Timestamp`")
    void timestamp() {
        Timestamp timestamp = Time.currentTime();
        Temporal<?> temporal = Temporals.from(timestamp);
        assertEquals(timestamp, temporal.toTimestamp());
    }

    @Test
    @DisplayName("create a `Temporal` from a temporal message")
    void message() {
        TemporalMessage<?> msg = LocalDates.of(1961, Month.APRIL, 12);
        Temporal<?> temporal = Temporals.from(msg);
        assertSame(msg, temporal);
    }

    @Test
    @DisplayName("fail for unknown types")
    void failOtherwise() {
        assertThrows(IllegalArgumentException.class,
                     () -> Temporals.from(Empty.getDefaultInstance()));
    }

    @Test
    @DisplayName("create a `Temporal` from an `Instant`")
    void createFromInstant() {
        Instant instant = Instant.now();
        Temporal<?> timestamp = Temporals.from(instant);

        assertEqual(timestamp, instant);
    }
}
