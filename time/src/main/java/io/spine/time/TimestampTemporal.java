/*
 * Copyright 2019, TeamDev. All rights reserved.
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

import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import io.spine.annotation.Internal;
import io.spine.protobuf.AnyPacker;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.protobuf.util.Timestamps.checkValid;

/**
 * An implementation of {@link Temporal} for the Protobuf {@link Timestamp}.
 */
@Internal
public final class TimestampTemporal implements Temporal<TimestampTemporal> {

    private final Timestamp value;

    private TimestampTemporal(Timestamp value) {
        this.value = value;
    }

    /**
     * Creates a new instance with the given {@code Timestamp}.
     *
     * <p>The given value must be valid in terms of {@code Timestamps.checkValid(..)}. Otherwise,
     * as {@code IllegalStateException} is thrown.
     */
    static TimestampTemporal from(Timestamp value) {
        checkNotNull(value);
        checkValid(value);
        return new TimestampTemporal(value);
    }

    @Override
    public Timestamp toTimestamp() {
        return value;
    }

    @Override
    public Any toAny() {
        return AnyPacker.pack(value);
    }
}