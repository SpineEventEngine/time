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

package io.spine.time;

import io.spine.annotation.GeneratedMixin;

import java.time.Instant;

import static io.spine.time.LocalDates.checkDate;
import static io.spine.time.LocalDates.converter;
import static java.time.ZoneOffset.UTC;
import static java.util.Objects.requireNonNull;

/**
 * An implementation of {@link io.spine.time.Temporal Temporal} based on {@link LocalDate}.
 */
@GeneratedMixin
interface LocalDateTemporal extends TemporalMessage<LocalDate>, LocalDateOrBuilder {

    /**
     * Obtains the year part of this date.
     */
    default int year() {
        return getYear();
    }

    /**
     * Obtains the month part of this date.
     *
     * @see #monthNumber()
     */
    default Month month() {
        return getMonth();
    }

    /**
     * Obtains a number of the month of this date.
     *
     * @see #month()
     */
    default int monthNumber() {
        return getMonthValue();
    }

    @Override
    default Instant toInstant() {
        var localDate = java.time.LocalDate.of(year(), monthNumber(), day());
        var instant = localDate.atStartOfDay().toInstant(UTC);
        return instant;
    }

    /**
     * Obtains number of the day in month.
     *
     * <p>The returned value is from 1 to 31 and is valid for the year and month.
     */
    default int day() {
        return getDay();
    }

    /**
     * Converts this date to Java Time instance.
     */
    default java.time.LocalDate toJavaTime() {
        @SuppressWarnings("ClassReferencesSubclass") // OK for mixins
        var self = (LocalDate) this;
        try {
            checkDate(self);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        }
        var result = converter().reverse().convert(self);
        return requireNonNull(result);
    }
}
