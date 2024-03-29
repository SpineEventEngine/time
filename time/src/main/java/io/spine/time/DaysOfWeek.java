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

import io.spine.time.string.TimeStringifiers;
import io.spine.util.SerializableConverter;

import java.time.DateTimeException;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.util.Exceptions.illegalArgumentWithCauseOf;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.util.Objects.requireNonNull;

/**
 * Utilities for working with {@link io.spine.time.DayOfWeek DayOfWeek} instances.
 */
public final class DaysOfWeek {

    /** Prevents instantiation of this utility class. */
    private DaysOfWeek() {
    }

    /**
     * Obtains the week day corresponding to the passed Java Time value.
     */
    public static DayOfWeek of(java.time.DayOfWeek day) {
        checkNotNull(day);
        var result = converter().convert(day);
        return requireNonNull(result);
    }

    private static void checkDay(int day) {
        try {
            DAY_OF_WEEK.checkValidValue(day);
        } catch (DateTimeException e) {
            throw illegalArgumentWithCauseOf(e);
        }
    }

    /**
     * Converts the passed instance to Java Time value.
     */
    public static java.time.DayOfWeek toJavaTime(DayOfWeek day) {
        checkNotNull(day);
        checkDay(day.getNumber());
        var result = converter().reverse().convert(day);
        return requireNonNull(result);
    }

    /**
     * Obtains string representation of the passed day of week.
     *
     * <p>Returned string is an internal representation, and should not be used
     * in the user interface.
     *
     * <p>For displaying a day of week, please use
     * {@link java.time.DayOfWeek#getDisplayName(java.time.format.TextStyle, java.util.Locale)
     *  java.time.DayOfWeek.getDisplayName(TextStyle, Locale)}.
     *
     * @see #parse(String)
     */
    public static String toString(DayOfWeek value) {
        checkNotNull(value);
        var result = TimeStringifiers.forDayOfWeek().convert(value);
        return requireNonNull(result);
    }

    /**
     * Parses a day of week value from an internal representation string.
     *
     * @see #toString(DayOfWeek)
     */
    public static DayOfWeek parse(String str) {
        checkNotNull(str);
        var converter = TimeStringifiers.forDayOfWeek().reverse();
        return requireNonNull(converter.convert(str));
    }

    /**
     * Obtains the converter from Java Time.
     */
    public static SerializableConverter<java.time.DayOfWeek, DayOfWeek> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java Time and back.
     */
    private static final class JtConverter
            extends AbstractConverter<java.time.DayOfWeek, DayOfWeek> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        private JtConverter() {
            super("DaysOfWeek.converter()");
        }

        @Override
        protected DayOfWeek doForward(java.time.DayOfWeek day) {
            var result = DayOfWeek.forNumber(day.getValue());
            return requireNonNull(result);
        }

        @Override
        protected java.time.DayOfWeek doBackward(DayOfWeek day) {
            var result = java.time.DayOfWeek.of(day.getNumber());
            return result;
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
