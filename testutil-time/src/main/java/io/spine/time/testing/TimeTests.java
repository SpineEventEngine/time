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

package io.spine.time.testing;

import com.google.common.annotations.VisibleForTesting;
import io.spine.base.Time;

import java.time.LocalTime;

import static io.spine.base.Time.currentTime;
import static io.spine.base.Time.currentTimeZone;

/**
 * Utility class for working with time-related tests.
 */
@VisibleForTesting
public final class TimeTests {

    /**
     * Prevents instantiation of this utility class.
     */
    private TimeTests() {
    }

    /**
     * Returns the {@linkplain Time#currentTime() current time} in seconds.
     *
     * @return a seconds value
     */
    public static long currentTimeSeconds() {
        long secs = currentTime().getSeconds();
        return secs;
    }

    /**
     * Waits till new day to come, if it's the last day second.
     *
     * <p>This method is useful for tests that obtain current date/time values
     * and need to avoid the day edge for correctness of the test values.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public static void avoidDayEdge() {
        LocalTime lastDaySecond = LocalTime.MAX.withNano(0);
        do {
            // Wait.
        } while (LocalTime.now(currentTimeZone())
                          .isAfter(lastDaySecond));
    }
}
