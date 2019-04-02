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

import com.google.protobuf.Timestamp;

import java.time.Instant;

import static io.spine.time.Timestamps2.fromInstant;

/**
 * An implementation of {@link io.spine.time.Temporal} based on {@link OffsetDateTime}.
 *
 * <p>This interface is designed to be implemented by {@code io.spine.time.OffsetDateTime}
 * exclusively. The interface does not add any abstract methods to its message counterpart.
 */
interface OffsetDateTimeTemporal extends TemporalMessage<OffsetDateTime>, OffsetDateTimeOrBuilder {

    @Override
    default Timestamp toTimestamp() {
        Instant instant = java.time.OffsetDateTime
                .of(LocalDateTimes.toJavaTime(getDateTime()),
                    ZoneOffsets.toJavaTime(getOffset()))
                .toInstant();
        Timestamp result = fromInstant(instant);
        return result;
    }
}
