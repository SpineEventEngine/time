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

package io.spine.time;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import io.spine.annotation.Internal;
import io.spine.type.TypeName;

import java.time.Instant;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.JavaTimeExtensions.toTimestamp;
import static io.spine.util.Exceptions.newIllegalArgumentException;

/**
 * A factory of {@link Temporal} instances.
 */
@Internal
public final class Temporals {

    /**
     * Prevents the utility class instantiation.
     */
    private Temporals() {
    }

    /**
     * Produces an instance of {@code Temporal} from the given message.
     *
     * <p>If the given message is a {@link Timestamp}, produces a wrapper {@code Temporal} instance.
     * If the given message is a {@code Temporal}, returns it without a change. Otherwise, throws
     * an {@code IllegalArgumentException}.
     *
     * @param value
     *         message to convert
     * @return instance of {@code Temporal}
     */
    @SuppressWarnings("ChainOfInstanceofChecks") // Creating an abstraction over all the time types.
    public static Temporal<?> from(Message value) {
        checkNotNull(value);
        if (value instanceof Temporal) {
            return (Temporal<?>) value;
        } else if (value instanceof Timestamp) {
            var timestampValue = (Timestamp) value;
            return TimestampTemporal.from(timestampValue);
        } else {
            throw newIllegalArgumentException(
                    "The type `%s` cannot represent a point in time.",
                    TypeName.of(value)
            );
        }
    }

    /**
     * Produces an instance of {@code Temporal} from the given {@link java.time.Instant}.
     *
     * @param instant
     *         the instance of {@code Instant} to convert into a {@code Temporal}
     * @return instance of {@code Temporal}
     * @implSpec Converts the given {@code Instant} into a {@code Timestamp} and generates
     *         a {@code Temporal} from the {@code Timestamp}
     */
    public static Temporal<?> from(Instant instant) {
        checkNotNull(instant);
        var timestamp = toTimestamp(instant);
        return from(timestamp);
    }
}
