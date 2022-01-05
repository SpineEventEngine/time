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

import io.spine.annotation.GeneratedMixin;

import java.time.Instant;

import static io.spine.time.LocalDateTimes.converter;
import static java.time.ZoneOffset.UTC;

/**
 * An implementation of {@link Temporal} based on {@link LocalDateTime}.
 */
@GeneratedMixin
interface LocalDateTimeTemporal extends TemporalMessage<LocalDateTime>, LocalDateTimeOrBuilder {

    @Override
    default Instant toInstant() {
        Instant instant = java.time.LocalDateTime
                .of(getDate().toJavaTime(), getTime().toJavaTime())
                .toInstant(UTC);
        return instant;
    }

    /** Obtains the date part of this date/time instance. */
    default LocalDate date() {
        return getDate();
    }

    /** Obtains the time part of this date/time instance. */
    default LocalTime time() {
        return getTime();
    }

    /**
     * Converts this date/time object to Java Time instance.
     */
    default java.time.LocalDateTime toJavaTime() {
        @SuppressWarnings("ClassReferencesSubclass") // OK for mixins
        LocalDateTime self = (LocalDateTime) this;
        return converter().reverse()
                          .convert(self);
    }
}
