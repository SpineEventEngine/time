/*
 * Copyright 2021, TeamDev. All rights reserved.
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
import io.spine.base.Time;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The provider of current time, which is always the same.
 *
 * <p>Use this {@code Timestamps.Provider} in time-related tests that are
 * sensitive to bounds of minutes, hours, days, etc.
 */
@VisibleForTesting
public class FrozenMadHatterParty implements Time.Provider {

    private final Timestamp frozenTime;

    /**
     * Creates a new party with the given time.
     */
    public FrozenMadHatterParty(Timestamp frozenTime) {
        this.frozenTime = checkNotNull(frozenTime);
    }

    /** Returns the value passed to the constructor. */
    @Override
    public Timestamp currentTime() {
        return frozenTime;
    }
}
