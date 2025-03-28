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

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Timestamp;

import static com.google.protobuf.util.Durations.fromMinutes;
import static com.google.protobuf.util.Timestamps.subtract;
import static io.spine.base.Time.currentTime;
import static io.spine.protobuf.Durations2.seconds;
import static io.spine.util.Preconditions2.checkPositive;

/**
 * Utility class for working with timestamps in the past.
 */
@VisibleForTesting
public final class Past {

    /**
     * Prevents instantiation of this utility class.
     */
    private Past() {
    }

    /**
     * The testing assistance utility, which returns a timestamp of the moment
     * of the passed number of minutes from now.
     *
     * @param value
     *         a positive number of minutes
     * @return a timestamp instance
     */
    public static Timestamp minutesAgo(int value) {
        checkPositive(value);
        var currentTime = currentTime();
        var result = subtract(currentTime, fromMinutes(value));
        return result;
    }

    /**
     * Obtains timestamp in the past a number of seconds ago.
     *
     * @param value
     *         a positive number of seconds
     * @return the moment `value` seconds ago
     */
    public static Timestamp secondsAgo(long value) {
        checkPositive(value);
        var currentTime = currentTime();
        var result = subtract(currentTime, seconds(value));
        return result;
    }
}
