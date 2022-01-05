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
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import io.spine.base.Time;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.protobuf.util.Durations.fromSeconds;
import static com.google.protobuf.util.Timestamps.add;
import static io.spine.base.Time.currentTime;
import static io.spine.base.Time.systemTime;
import static io.spine.util.Preconditions2.checkPositive;

/**
 * Utility class for working with timestamps of the the future.
 */
@VisibleForTesting
public final class Future {

    /**
     * Prevents instantiation of this utility class.
     */
    private Future() {
    }

    /**
     * Obtains timestamp in the future a number of seconds from current time.
     *
     * @param seconds
     *         a positive number of seconds
     * @return the moment which is {@code seconds} from now
     */
    public static Timestamp secondsFromNow(long seconds) {
        checkPositive(seconds);
        var currentTime = currentTime();
        var result = add(currentTime, fromSeconds(seconds));
        return result;
    }

    /**
     * Verifies if the passed timestamp is in the future comparing it
     * with {@linkplain Time#systemTime() system time}.
     */
    public static boolean isFuture(Timestamp timestamp) {
        checkNotNull(timestamp);
        // Do not use `currentTime()` as we may use custom `TimestampProvider` already.
        // Get time from metal.
        var currentSystemTime = systemTime();

        // NOTE: we have the risk of having these two timestamps too close to each other
        // so that the passed timestamp becomes "the past" around the time of this call.
        // To avoid this, select some time in the "distant" future.
        var result = Timestamps.comparator()
                               .compare(currentSystemTime, timestamp) < 0;
        return result;
    }
}
