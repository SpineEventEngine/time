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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.Calendars.toCalendar;
import static io.spine.time.DtPreconditions.checkArguments;
import static io.spine.time.Formats.appendSubSecond;
import static io.spine.time.Formats.appendZoneOffset;
import static io.spine.time.Formats.dateTimeFormat;
import static io.spine.time.ZoneOffsets.adjustZero;

/**
 * Routines for working with {@link OffsetDateTime}.
 *
 * @author Alexander Aleksandrov
 * @author Alexander Yevsyukov
 */
@SuppressWarnings("ClassWithTooManyMethods")
public final class OffsetDateTimes {

    /** Prevent instantiation of this utility class. */
    private OffsetDateTimes() {
    }

    /**
     * Obtains current date/time at the passed time zone.
     */
    public static OffsetDateTime now(ZoneOffset zoneOffset) {
        checkNotNull(zoneOffset);
        java.time.OffsetDateTime now = java.time.OffsetDateTime.now();
        LocalTime localTime = LocalTimes.of(now.toLocalTime());
        LocalDate localDate = LocalDates.of(now.toLocalDate());
        return create(localDate, localTime, zoneOffset);
    }

    /**
     * Creates a new instance with the passed values.
     */
    public static OffsetDateTime of(LocalDate date, LocalTime time, ZoneOffset offset) {
        return create(date, time, offset);
    }

    /**
     * Creates new instance based on the passed Java Time value.
     */
    public static OffsetDateTime of(java.time.OffsetDateTime jdt) {
        java.time.LocalDate ld = jdt.toLocalDate();
        java.time.LocalTime lt = jdt.toLocalTime();
        java.time.ZoneOffset zo = jdt.toZonedDateTime().getOffset();
        return create(LocalDates.of(ld),
                      LocalTimes.of(lt),
                      ZoneOffsets.of(zo));
    }

    private static OffsetDateTime create(LocalDate date, LocalTime time, ZoneOffset offset) {
        OffsetDateTime.Builder result = OffsetDateTime
                .newBuilder()
                .setDate(date)
                .setTime(time)
                .setOffset(adjustZero(offset));
        return result.build();
    }

    private static java.time.OffsetDateTime toJavaTime(OffsetDateTime value) {
        java.time.OffsetDateTime result = java.time.OffsetDateTime.of(
                LocalDates.toJavaTime(value.getDate()),
                LocalTimes.toJavaTime(value.getTime()),
                ZoneOffsets.toJavaTime(value.getOffset())
        );
        return result;
    }

    /**
     * Obtains a copy of the passed value with added number of years.
     *
     * @param value      the value to update
     * @param yearsToAdd a positive number of years to add
     */
    public static OffsetDateTime addYears(OffsetDateTime value, int yearsToAdd) {
        checkArguments(value, yearsToAdd);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.plusYears(yearsToAdd));
    }

    /**
     * Obtains a copy of the passed value with the specified number of months added.
     *
     * @param value       the value to update
     * @param monthsToAdd a positive number of months to add
     */
    public static OffsetDateTime addMonths(OffsetDateTime value, int monthsToAdd) {
        checkArguments(value, monthsToAdd);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.plusMonths(monthsToAdd));
    }

    /**
     * Obtains a copy of the passed value with the specified number of days added.
     *
     * @param value     the value to update
     * @param daysToAdd a positive number of days to add
     */
    public static OffsetDateTime addDays(OffsetDateTime value, int daysToAdd) {
        checkArguments(value, daysToAdd);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.plusDays(daysToAdd));
    }

    /**
     * Obtains a copy of the passed value with the specified number of hours added.
     *
     * @param value      the value to update
     * @param hoursToAdd a positive number of hours to add
     */
    public static OffsetDateTime addHours(OffsetDateTime value, int hoursToAdd) {
        checkArguments(value, hoursToAdd);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.plusHours(hoursToAdd));
    }

    /**
     * Obtains a copy of the passed value with the specified number of minutes added.
     *
     * @param value        the value to update
     * @param minutesToAdd a positive number of minutes to add
     */
    public static OffsetDateTime addMinutes(OffsetDateTime value, int minutesToAdd) {
        checkArguments(value, minutesToAdd);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.plusMinutes(minutesToAdd));
    }

    /**
     * Obtains a copy of the passed value with the specified number of seconds added.
     *
     * @param value        the value to update
     * @param secondsToAdd a positive number of seconds to add
     */
    public static OffsetDateTime addSeconds(OffsetDateTime value, int secondsToAdd) {
        checkArguments(value, secondsToAdd);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.plusSeconds(secondsToAdd));
    }

    /**
     * Obtains a copy of the passed value with the specified number of nanoseconds added.
     *
     * @param value       the value to update
     * @param nanosToAdd a positive number of nanoseconds to add
     */
    public static OffsetDateTime addNanos(OffsetDateTime value, int nanosToAdd) {
        checkArguments(value, nanosToAdd);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.plusNanos(nanosToAdd));
    }

    /**
     * Obtains a copy of the passed value with the specified number of years subtracted.
     *
     * @param value           the value to update
     * @param yearsToSubtract a number of years to subtract
     */
    public static OffsetDateTime subtractYears(OffsetDateTime value, int yearsToSubtract) {
        checkArguments(value, yearsToSubtract);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.minusYears(yearsToSubtract));
    }

    /**
     * Obtains a copy of the passed value with the specified number of months subtracted.
     *
     * @param value            the value to update
     * @param monthsToSubtract a number of months to subtract
     */
    public static OffsetDateTime subtractMonths(OffsetDateTime value, int monthsToSubtract) {
        checkArguments(value, monthsToSubtract);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.minusMonths(monthsToSubtract));
    }

    /**
     * Obtains a copy of the passed value with the specified number of days subtracted.
     *
     * @param value          the value to update
     * @param daysToSubtract a number of days to subtract
     */
    public static OffsetDateTime subtractDays(OffsetDateTime value, int daysToSubtract) {
        checkArguments(value, daysToSubtract);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.minusDays(daysToSubtract));
    }

    /**
     * Obtains a copy of the passed value with the specified number of hours subtracted.
     *
     * @param value           the value to update
     * @param hoursToSubtract a number of hours to subtract
     */
    public static OffsetDateTime subtractHours(OffsetDateTime value, int hoursToSubtract) {
        checkArguments(value, hoursToSubtract);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.minusHours(hoursToSubtract));
    }

    /**
     * Obtains a copy of the passed value with the specified number of minutes subtracted.
     *
     * @param value             the value to update
     * @param minutesToSubtract a positive number of minutes to subtract
     */
    public static OffsetDateTime subtractMinutes(OffsetDateTime value, int minutesToSubtract) {
        checkArguments(value, minutesToSubtract);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.minusMinutes(minutesToSubtract));
    }

    /**
     * Obtains a copy of the passed value with the specified number of seconds subtracted.
     *
     * @param value             the value to update
     * @param secondsToSubtract a number of seconds to subtract
     */
    public static OffsetDateTime subtractSeconds(OffsetDateTime value, int secondsToSubtract) {
        checkArguments(value, secondsToSubtract);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.minusSeconds(secondsToSubtract));
    }

    /**
     * Obtains a copy of the passed value with the specified number of nanoseconds subtracted.
     *
     * @param value            the value to update
     * @param nanosToSubtract a number of milliseconds to subtract
     */
    public static OffsetDateTime subtractNanos(OffsetDateTime value, long nanosToSubtract) {
        checkArguments(value, nanosToSubtract);
        java.time.OffsetDateTime jdt = toJavaTime(value);
        return of(jdt.minusNanos(nanosToSubtract));
    }

    /**
     * Returns a ISO 8601 date/time string corresponding to the passed value.
     */
    public static String toString(OffsetDateTime value) {
        Calendar calendar = toCalendar(value);
        ZoneOffset offset = value.getOffset();
        StringBuilder result = new StringBuilder();

        // Format the date/time part.
        Date date = calendar.getTime();
        String dateTime = dateTimeFormat(offset).format(date);
        result.append(dateTime);

        // Format the fractional second part.
        LocalTime time = value.getTime();
        appendSubSecond(result, time);

        // Add time zone.
        appendZoneOffset(result, offset);

        return result.toString();
    }

    /**
     * Parse from ISO 8601 date/time string to {@code OffsetDateTime}.
     *
     * @throws ParseException if the passed string is not a valid date-time value
     */
    public static OffsetDateTime parse(String value) throws ParseException {
        return Parser.parseOffsetDateTime(value);
    }
}
