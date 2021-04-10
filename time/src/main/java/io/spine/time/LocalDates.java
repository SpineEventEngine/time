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

import io.spine.string.Stringifier;
import io.spine.time.string.TimeStringifiers;
import io.spine.util.SerializableConverter;

import java.time.DateTimeException;
import java.time.YearMonth;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.DtPreconditions.checkPositive;
import static io.spine.time.Months.checkMonth;
import static io.spine.util.Exceptions.illegalArgumentWithCauseOf;
import static java.lang.String.format;
import static java.time.temporal.ChronoField.YEAR;
import static java.util.Objects.requireNonNull;

/**
 * Utilities for working with {@link LocalDate}.
 */
public final class LocalDates {

    /** Prevent instantiation of this utility class. */
    private LocalDates() {
    }

    /**
     * Creates an instance by the passed Java Time value.
     */
    public static LocalDate of(java.time.LocalDate ld) {
        checkNotNull(ld);
        LocalDate result = converter().convert(ld);
        return requireNonNull(result);
    }

    /**
     * Converts the passed value to Java Time instance.
     */
    public static java.time.LocalDate toJavaTime(LocalDate date) {
        checkDate(date);
        java.time.LocalDate result =
                converter().reverse()
                           .convert(date);
        return requireNonNull(result);
    }

    /**
     * Obtains local date from a year, month, and day.
     */
    public static LocalDate of(int year, Month month, int day) {
        checkNotNull(month);
        checkPositive(year);
        checkPositive(day);
        checkDate(year, month, day);

        LocalDate result = LocalDate
                .newBuilder()
                .setYear(year)
                .setMonth(month)
                .setDay(day)
                .vBuild();
        return result;
    }

    /**
     * Parse from ISO-8601 date representation of the format {@code yyyy-MM-dd}.
     *
     * @see #toString(LocalDate)
     */
    public static LocalDate parse(String str) {
        checkNotNull(str);
        LocalDate result =
                stringifier().reverse()
                             .convert(str);
        return requireNonNull(result);
    }

    private static Stringifier<LocalDate> stringifier() {
        return TimeStringifiers.forLocalDate();
    }

    /**
     * Converts a local date into ISO-8601 string with the format {@code yyyy-MM-dd}.
     *
     * @see #parse(String)
     */
    public static String toString(LocalDate date) {
        checkDate(date);
        String result = stringifier().convert(date);
        return requireNonNull(result);
    }

    /**
     * Verified fields of the passed local date instance.
     *
     * @param date
     *         the date to check
     * @throws IllegalArgumentException
     *         if one of the date values has an invalid value
     */
    static void checkDate(LocalDate date) {
        checkNotNull(date);
        checkDate(date.getYear(), date.getMonth(), date.getDay());
    }

    /**
     * Ensures that the passed date is valid.
     *
     * <p>Verifies that:
     * <ul>
     *     <li>the year is within the {@linkplain java.time.Year#MIN_VALUE min}/
     *     {@linkplain java.time.Year#MAX_VALUE max} range,
     *     <li>the month is not in the range of {@code JANUARY} to {@code DECEMBER},
     *     <li>the day is less or equal zero or greater than can be in the month.
     * </ul>
     *
     * @throws IllegalArgumentException
     *         if one of the arguments is invalid
     */
    private static void checkDate(int year, Month month, int day) {
        try {
            YEAR.checkValidValue(year);
        } catch (DateTimeException e) {
            throw illegalArgumentWithCauseOf(e);
        }

        checkNotNull(month);
        checkMonth(month.getNumber());
        checkPositive(day);

        final int daysInMonth = YearMonth.of(year, month.getNumber())
                                         .lengthOfMonth();
        if (day > daysInMonth) {
            final String errMsg = format(
                    "A number of days cannot be more than %d, for this month and year.",
                    daysInMonth);
            throw new IllegalArgumentException(errMsg);
        }
    }

    /**
     * Obtains Java Time converter instance.
     */
    public static SerializableConverter<java.time.LocalDate, LocalDate> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java time and back.
     */
    private static final class JtConverter
            extends AbstractConverter<java.time.LocalDate, LocalDate> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        private JtConverter() {
            super("LocalDates.converter()");
        }

        @Override
        protected LocalDate doForward(java.time.LocalDate date) {
            LocalDate result = LocalDate
                    .newBuilder()
                    .setYear(date.getYear())
                    .setMonth(Months.of(date))
                    .setDay(date.getDayOfMonth())
                    .vBuild();
            return result;
        }

        @Override
        protected java.time.LocalDate doBackward(LocalDate date) {
            java.time.LocalDate result = java.time.LocalDate.of(
                    date.getYear(),
                    date.getMonthValue(),
                    date.getDay()
            );
            return result;
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
