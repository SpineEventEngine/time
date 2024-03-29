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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility class for working with date-time changes.
 */
public final class TimeChanges {

    /** Prevents instantiation of this utility class. */
    private TimeChanges() {
    }

    /**
     * Creates {@link LocalDateChange} object for the passed previous and new field values of
     * local date.
     *
     * <p>Passed values cannot be equal.
     */
    public static LocalDateChange of(LocalDate previousValue, LocalDate newValue) {
        checkNotEqual(previousValue, newValue);

        var result = LocalDateChange.newBuilder()
                .setPreviousValue(previousValue)
                .setNewValue(newValue)
                .build();
        return result;
    }

    /**
     * Creates {@link LocalTimeChange} object for the passed previous and new field values of
     * local time.
     *
     * <p>Passed values cannot be equal.
     */
    public static LocalTimeChange of(LocalTime previousValue, LocalTime newValue) {
        checkNotEqual(previousValue, newValue);

        var result = LocalTimeChange.newBuilder()
                .setPreviousValue(previousValue)
                .setNewValue(newValue)
                .build();
        return result;
    }

    /**
     * Creates {@link OffsetTimeChange} object for the passed previous and new field values of
     * offset time.
     *
     * <p>Passed values cannot be equal.
     *
     * @deprecated please use {@link ZonedDateTime} with a custom change type instead.
     */
    @Deprecated
    public static OffsetTimeChange of(OffsetTime previousValue, OffsetTime newValue) {
        checkNotEqual(previousValue, newValue);

        var result = OffsetTimeChange.newBuilder()
                .setPreviousValue(previousValue)
                .setNewValue(newValue)
                .build();
        return result;
    }

    /**
     * Creates {@link OffsetDateTimeChange} object for the passed previous and new field values of
     * offset time.
     *
     * <p>Passed values cannot be equal.
     *
     * @deprecated please use {@link ZonedDateTime} with a custom change type instead.
     */
    @Deprecated
    public static OffsetDateTimeChange of(OffsetDateTime previousValue, OffsetDateTime newValue) {
        checkNotEqual(previousValue, newValue);

        var result = OffsetDateTimeChange.newBuilder()
                .setPreviousValue(previousValue)
                .setNewValue(newValue)
                .build();
        return result;
    }

    /**
     * Ensures that parameters are not equal.
     *
     * @throws IllegalArgumentException
     *         in case if values are equal
     */
    private static <T> void checkNotEqual(T previousValue, T newValue) {
        checkNotNull(previousValue);
        checkNotNull(newValue);
        checkArgument(!newValue.equals(previousValue),
                      "newValue cannot be equal to previousValue");
    }
}
