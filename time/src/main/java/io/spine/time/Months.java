/*
 * Copyright 2020, TeamDev. All rights reserved.
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
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;

/**
 * Utilities for working with calendar months.
 */
public final class Months {

    /** Prevent instantiation of this utility class. */
    private Months() {
    }

    static void checkMonth(int month) {
        try {
            MONTH_OF_YEAR.checkValidValue(month);
        } catch (DateTimeException e) {
            throw illegalArgumentWithCauseOf(e);
        }
    }

    /**
     * Creates an instance by the passed number.
     */
    public static Month of(int month) {
        checkMonth(month);
        return Month.forNumber(month);
    }

    /**
     * Obtains the month of the passed date.
     */
    public static Month of(java.time.LocalDate date) {
        checkNotNull(date);
        return converter().convert(date.getMonth());
    }

    /**
     * Converts the passed Java Time value.
     */
    public static Month of(java.time.Month month) {
        checkNotNull(month);
        return converter().convert(month);
    }

    /**
     * Converts the passed instance to the Java Time value.
     */
    public static java.time.Month toJavaTime(Month value) {
        checkNotNull(value);
        checkMonth(value.getNumber());
        return converter().reverse()
                          .convert(value);
    }

    /**
     * Obtains the instance of Java Time converter.
     */
    public static SerializableConverter<java.time.Month, Month> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Obtains string representation of the passed month.
     *
     * <p>Returned string is an internal representation, and should not be used in
     * the user interface.
     *
     * <p>For displaying a month, please use
     * {@link java.time.Month#getDisplayName(java.time.format.TextStyle, java.util.Locale)
     * java.time.Month.getDisplayName(TextStyle, Locale)}.
     *
     * @see #parse(String)
     */
    public static String toString(Month value) {
        checkNotNull(value);
        return TimeStringifiers.forMonth()
                               .convert(value);
    }

    /**
     * Parses a month from an internal representation string.
     *
     * @see #toString(Month)
     */
    public static Month parse(String str) {
        checkNotNull(str);
        return TimeStringifiers.forMonth()
                               .reverse()
                               .convert(str);
    }

    /**
     * Converts from Java Time and back.
     */
    private static final class JtConverter extends AbstractConverter<java.time.Month, Month> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        private JtConverter() {
            super("Months.converter()");
        }

        @Override
        protected Month doForward(java.time.Month month) {
            Month result = Month.forNumber(month.getValue());
            return result;
        }

        @Override
        protected java.time.Month doBackward(Month month) {
            return java.time.Month.of(month.getNumber());
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
