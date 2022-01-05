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
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.protobuf.Timestamp;
import io.spine.base.Time;

import static com.google.protobuf.util.Timestamps.add;
import static com.google.protobuf.util.Timestamps.subtract;
import static io.spine.base.Time.systemTime;
import static io.spine.protobuf.Durations2.hours;
import static io.spine.util.Preconditions2.checkPositive;

/**
 * The time provider that can rewind current time.
 *
 * <p>Created in the future, {@linkplain #THIRTY_YEARS_IN_HOURS 30 years} from
 * the {@link Time#systemTime() current system time}.
 */
@VisibleForTesting
public class BackToTheFuture implements Time.Provider {

    public static final long THIRTY_YEARS_IN_HOURS = 262800L;

    private Timestamp currentTime;

    public BackToTheFuture() {
        this.currentTime = add(systemTime(), hours(THIRTY_YEARS_IN_HOURS));
    }

    @Override
    public synchronized Timestamp currentTime() {
        return this.currentTime;
    }

    private synchronized void setCurrentTime(Timestamp currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * Rewinds the {@linkplain #currentTime() "current time"} forward
     * by the passed amount of hours.
     */
    @CanIgnoreReturnValue
    public synchronized Timestamp forward(long hoursDelta) {
        checkPositive(hoursDelta);
        Timestamp newTime = add(this.currentTime, hours(hoursDelta));
        setCurrentTime(newTime);
        return newTime;
    }

    /**
     * Rewinds the {@linkplain #currentTime() "current time"} backward
     * by the passed amount of hours.
     */
    @CanIgnoreReturnValue
    public synchronized Timestamp backward(long hoursDelta) {
        checkPositive(hoursDelta);
        Timestamp newTime = subtract(this.currentTime, hours(hoursDelta));
        setCurrentTime(newTime);
        return newTime;
    }
}
