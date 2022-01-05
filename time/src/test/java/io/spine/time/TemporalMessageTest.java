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

import io.spine.protobuf.AnyPacker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.truth.extensions.proto.ProtoTruth.assertThat;

/**
 * The implementation base for test suites covering {@link io.spine.time.Temporal}
 * implementations.
 *
 * @param <T>
 *         the type of {@link io.spine.time.Temporal}
 */
abstract class TemporalMessageTest<T extends TemporalMessage<T>> {

    private final Class<T> type;

    /**
     * Constructs the test suite.
     *
     * @param type
     *         the exact runtime type of {@link io.spine.time.Temporal} under the test
     */
    protected TemporalMessageTest(Class<T> type) {
        this.type = checkNotNull(type);
    }

    /**
     * Creates a new non-default valid instance of {@link io.spine.time.Temporal}.
     *
     * <p>A typical implementation obtains the current time value.
     */
    abstract T create();

    @Test
    @DisplayName("convert self to a `Timestamp`")
    void toTimestamp() {
        var temporal = create();
        var timestamp = temporal.toTimestamp();
        var assertTimestamp = assertThat(timestamp);
        assertTimestamp.isNotNull();
        assertTimestamp.isNotEqualToDefaultInstance();
    }

    @Test
    @DisplayName("pack self to Any")
    void toAny() {
        var temporal = create();
        var any = temporal.toAny();
        var assertAny = assertThat(any);
        assertAny.isNotNull();
        assertAny.isNotEqualToDefaultInstance();

        var unpacked = AnyPacker.unpack(any);
        assertThat(unpacked).isInstanceOf(type);
    }
}
