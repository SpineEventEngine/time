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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("BackToTheFuture should")
class BackToTheFutureTest {

    private BackToTheFuture timeProvider;

    @BeforeEach
    void setUp() {
        timeProvider = new BackToTheFuture();
    }

    @Test
    @DisplayName("provide time from the future")
    void futureTime() {
        assertTrue(Future.isFuture(timeProvider.currentTime()));
    }

    @Test
    @DisplayName("rewind time backward")
    void rewindBackward() {
        // Rewind to somewhere around present.
        assertNotEquals(timeProvider.currentTime(),
                        timeProvider.backward(BackToTheFuture.THIRTY_YEARS_IN_HOURS));

        // ... and back to 30 years in the past.
        timeProvider.backward(BackToTheFuture.THIRTY_YEARS_IN_HOURS);

        assertFalse(Future.isFuture(timeProvider.currentTime()));
    }

    @SuppressWarnings("MagicNumber")
    @Test
    @DisplayName("rewind time forward")
    void rewindForward() {
        // Rewind to somewhere around present.
        timeProvider.backward(BackToTheFuture.THIRTY_YEARS_IN_HOURS);

        timeProvider.forward(BackToTheFuture.THIRTY_YEARS_IN_HOURS + 24L);

        assertTrue(Future.isFuture(timeProvider.currentTime()));
    }
}
