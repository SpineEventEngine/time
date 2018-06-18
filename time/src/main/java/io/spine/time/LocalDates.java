/*
 * Copyright 2018, TeamDev. All rights reserved.
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

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.DtPreconditions.checkPositive;
import static java.lang.String.format;

/**
 * Utilities for working with {@link LocalDate}.
 *
 * @author Alexander Yevsyukov
 * @author Alexander Aleksandrov
 */
public final class LocalDates {

    /** Prevent instantiation of this utility class. */
    private LocalDates() {
    }

    /**
     * Obtains current local date.
     */
    public static LocalDate now() {
        java.time.LocalDate now = java.time.LocalDate.now();
        return of(now);
    }

    /**
     * Creates an instance by the passed Java Time value.
     */
    public static LocalDate of(java.time.LocalDate ld) {
        checkNotNull(ld);
        LocalDate.Builder result = LocalDate
                .newBuilder()
                .setYear(ld.getYear())
                .setMonth(Months.of(ld))
                .setDay(ld.getDayOfMonth());
        return result.build();
    }

    /**
     * Converts the passed value to Java Time instance.
     */
    public static java.time.LocalDate toJavaTime(LocalDate date) {
        java.time.LocalDate result = java.time.LocalDate.of(
                date.getYear(),
                date.getMonthValue(),
                date.getDay()
        );
        return result;
    }

    /**
     * Obtains local date from a year, month, and day.
     */
    public static LocalDate of(int year, Month month, int day) {
        checkPositive(year);
        checkPositive(day);
        checkDate(year, month, day);

        LocalDate result = LocalDate
                .newBuilder()
                .setYear(year)
                .setMonth(month)
                .setDay(day)
                .build();
        return result;
    }

    /**
     * Parse from ISO 8601 date representation of the format {@code yyyy-MM-dd}.
     *
     * @return a LocalDate parsed from the string
     */
    public static LocalDate parse(String str) {
        java.time.LocalDate parsed = java.time.LocalDate.parse(str);
        return of(parsed);
    }

    /**
     * Converts a local date into ISO 8601 string with the format {@code yyyy-MM-dd}.
     */
    public static String toString(LocalDate date) {
        checkNotNull(date);
        java.time.LocalDate ld = toJavaTime(date);
        return ld.toString();
    }

    /**
     * Verified fields of the passed local date instance.
     *
     * @param date the date to check
     * @throws IllegalArgumentException if one of the date values has an invalid value
     */
    static void checkDate(LocalDate date) {
        checkDate(date.getYear(), date.getMonth(), date.getDay());
    }

    /**
     * Ensures that the passed date is valid.
     *
     * @throws IllegalArgumentException if
     * <ul>
     *     <li>the year is less or equal zero,
     *     <li>the month is {@code UNDEFINED},
     *     <li>the day is less or equal zero or greater than can be in the month.
     * </ul>
     */
    private static void checkDate(int year, Month month, int day) {
        checkPositive(year);
        checkNotNull(month);
        checkPositive(month.getNumber());
        checkPositive(day);

        final int daysInMonth = Months.daysInMonth(year, month);

        if (day > daysInMonth) {
            final String errMsg = format(
                    "A number of days cannot be more than %d, for this month and year.",
                    daysInMonth);
            throw new IllegalArgumentException(errMsg);
        }
    }
}
