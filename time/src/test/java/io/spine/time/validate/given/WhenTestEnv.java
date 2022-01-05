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

package io.spine.time.validate.given;

import com.google.protobuf.Duration;
import com.google.protobuf.Timestamp;
import io.spine.base.Time;

import static com.google.protobuf.util.Timestamps.add;
import static com.google.protobuf.util.Timestamps.subtract;
import static io.spine.base.Time.currentTime;

public final class WhenTestEnv {

    /**
     * Prevents the utility class instantiation.
     */
    private WhenTestEnv() {
    }

    public static final int FIFTY_NANOSECONDS = 50;
    public static final int ZERO_NANOSECONDS = 0;

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_5_MINUTES = 5 * SECONDS_IN_MINUTE;

    /**
     * Freezes time for current thread by setting the time provider to a
     * {@link ConstantTimeProvider}.
     *
     * @param time
     *         time to be returned upon {@link Time#currentTime()} call.
     */
    public static void freezeTime(Timestamp time) {
        Time.setProvider(() -> time);
    }

    public static Timestamp future() {
        var future = add(currentTime(), newDuration(SECONDS_IN_5_MINUTES));
        return future;
    }

    public static Timestamp past() {
        var past = subtract(currentTime(), newDuration(SECONDS_IN_5_MINUTES));
        return past;
    }

    public static Timestamp currentTimeWithNanos(int nanos) {
        var result = timeWithNanos(currentTime(), nanos);
        return result;
    }

    public static Timestamp timeWithNanos(Timestamp time, int nanos) {
        var result = time.toBuilder()
                .setNanos(nanos)
                .build();
        return result;
    }

    public static Duration newDuration(int seconds) {
        var result = Duration.newBuilder()
                .setSeconds(seconds)
                .build();
        return result;
    }
}
