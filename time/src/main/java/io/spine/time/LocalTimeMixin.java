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

import static io.spine.time.LocalTimes.converter;
import static java.util.Objects.requireNonNull;

/**
 * A mixin for extending behaviour of {@link LocalTime}.
 *
 * @apiNote This interface does not extend {@link TemporalMessage} because local time cannot
 * be converted to a point in time without additional information such as date and time-zone.
 */
@GeneratedMixin
interface LocalTimeMixin extends LocalTimeOrBuilder {

    /** Obtains an hour of this local time, from 0 to 23. */
    default int hour() {
        return getHour();
    }

    /** Obtains minutes of this local time, from 0 to 59. */
    default int minute() {
        return getMinute();
    }

    /** Obtains seconds of a minute, from 0 to 59. */
    default int second() {
        return getSecond();
    }

    /** Obtains fractions of a second from 0 to 999,999,999. */
    default int nano() {
        return getNano();
    }

    /** Converts this time to a Java Time instance. */
    default java.time.LocalTime toJavaTime() {
        @SuppressWarnings("ClassReferencesSubclass") // OK for mixin.
        var self = (LocalTime) this;
        var result = converter().reverse().convert(self);
        return requireNonNull(result);
    }
}
