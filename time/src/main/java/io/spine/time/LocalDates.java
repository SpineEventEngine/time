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

import java.util.Calendar;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.Calendars.toLocalDate;
import static io.spine.time.DtPreconditions.checkArguments;
import static io.spine.time.DtPreconditions.checkPositive;
import static java.lang.String.format;
import static java.util.Calendar.getInstance;

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
        final Calendar cal = getInstance();
        final LocalDate result = toLocalDate(cal);
        return result;
    }

    public static LocalDate of(java.time.LocalDate ld) {
        checkNotNull(ld);
        LocalDate.Builder result = LocalDate
                .newBuilder()
                .setYear(ld.getYear())
                .setMonth(Months.of(ld))
                .setDay(ld.getDayOfMonth());
        return result.build();
    }

    static java.time.LocalDate toJavaTime(LocalDate date) {
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
    public static LocalDate of(int year, MonthOfYear month, int day) {
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
     * Obtains new local date with the specified number of years added.
     */
    public static LocalDate addYears(LocalDate localDate, int yearsToAdd) {
        checkArguments(localDate, yearsToAdd);
        java.time.LocalDate updated = toJavaTime(localDate).plusYears(yearsToAdd);
        return of(updated);
    }

    /**
     * Obtains new local date with the specified number of months added.
     */
    public static LocalDate addMonths(LocalDate localDate, int monthsToAdd) {
        checkArguments(localDate, monthsToAdd);
        java.time.LocalDate updated = toJavaTime(localDate).plusMonths(monthsToAdd);
        return of(updated);
    }

    /**
     * Obtains new local date with the specified number of days added.
     */
    public static LocalDate addDays(LocalDate localDate, int daysToAdd) {
        checkArguments(localDate, daysToAdd);
        java.time.LocalDate updated = toJavaTime(localDate).plusDays(daysToAdd);
        return of(updated);
    }

    /**
     * Obtains new local date with the specified number of years subtracted.
     */
    public static LocalDate subtractYears(LocalDate localDate, int yearsToSubtract) {
        checkArguments(localDate, yearsToSubtract);
        java.time.LocalDate updated = toJavaTime(localDate).minusYears(yearsToSubtract);
        return of(updated);
    }

    /**
     * Obtains new local date with the specified number of months subtracted.
     */
    public static LocalDate subtractMonths(LocalDate localDate, int monthsToSubtract) {
        checkArguments(localDate, monthsToSubtract);
        java.time.LocalDate updated = toJavaTime(localDate).minusMonths(monthsToSubtract);
        return of(updated);
    }

    /**
     * Obtains new local date with the specified number of days subtracted.
     */
    public static LocalDate subtractDays(LocalDate localDate, int daysToSubtract) {
        checkArguments(localDate, daysToSubtract);
        java.time.LocalDate updated = toJavaTime(localDate).minusDays(daysToSubtract);
        return of(updated);
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
    static void checkDate(int year, MonthOfYear month, int day) {
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
