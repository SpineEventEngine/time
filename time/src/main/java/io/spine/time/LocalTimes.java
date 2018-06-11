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

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import io.spine.base.Time;

import java.time.Instant;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.DtPreconditions.checkArguments;
import static io.spine.time.EarthTime.HOURS_PER_DAY;
import static io.spine.time.EarthTime.MINUTES_PER_HOUR;
import static io.spine.time.EarthTime.SECONDS_PER_MINUTE;
import static io.spine.time.SiTime.MILLIS_PER_SECOND;
import static io.spine.time.SiTime.NANOS_PER_SECOND;

/**
 * Routines for working with {@link LocalTime}.
 *
 * @author Alexander Aleksandrov
 * @author Alexander Yevsyukov
 */
@SuppressWarnings("ClassWithTooManyMethods") // OK for this utility class.
public final class LocalTimes {

    /** Prevent instantiation of this utility class. */
    private LocalTimes() {
    }

    /**
     * Obtains current local time.
     */
    public static LocalTime now() {
        final Timestamp time = Time.getCurrentTime();
        final ZoneOffset zoneOffset = ZoneOffsets.getDefault();
        return timeAt(time, zoneOffset);
    }

    /**
     * Obtains local time at the passed time zone.
     */
    public static LocalTime timeAt(Timestamp time, ZoneOffset zoneOffset) {
        Instant instant = Instant.ofEpochMilli(Timestamps.toMillis(time));
        java.time.ZoneOffset zo = ZoneOffsets.toJavaTime(zoneOffset);
        java.time.LocalTime lt = instant.atOffset(zo)
                                        .toLocalTime();
        return of(lt);
    }

    /**
     * Obtains local time from an hours, minutes, seconds, milliseconds, and nanoseconds.
     */
    public static LocalTime of(int hours, int minutes, int seconds, int nanos) {
        checkClockTime(hours, minutes, seconds);
        Parameter.NANOS.check(nanos);

        LocalTime result = LocalTime
                .newBuilder()
                .setHour(hours)
                .setMinute(minutes)
                .setSecond(seconds)
                .setNano(nanos)
                .build();
        return result;
    }

    /**
     * Obtains local time from time passed {@code java.time} value. 
     */
    public static LocalTime of(java.time.LocalTime value) {
        LocalTime result = LocalTime
                .newBuilder()
                .setHour(value.getHour())
                .setMinute(value.getMinute())
                .setSecond(value.getSecond())
                .setNano(value.getNano())
                .build();
        return result;
    }

    /**
     * Converts the passed value to corresponding Java Time instance.
     */
    public static java.time.LocalTime toJavaTime(LocalTime value) {
        checkNotNull(value);
        return java.time.LocalTime.of(value.getHour(),
                                      value.getMinute(),
                                      value.getSecond(),
                                      value.getNano());
    }

    private static void checkClockTime(int hours, int minutes, int seconds) {
        Parameter.HOURS.check(hours);
        Parameter.MINUTES.check(minutes);
        Parameter.SECONDS.check(seconds);
    }

    /**
     * Obtains local time from hours, minutes, and seconds.
     */
    public static LocalTime of(int hours, int minutes, int seconds) {
        return of(hours, minutes, seconds, 0);
    }

    /**
     * Obtains local time from hours and minutes.
     */
    public static LocalTime of(int hours, int minutes) {
        return of(hours, minutes, 0, 0);
    }

    /**
     * Obtains new local time with specified number of hours added.
     */
    public static LocalTime addHours(LocalTime localTime, int hoursToAdd) {
        checkArguments(localTime, hoursToAdd);
        return of(toJavaTime(localTime).plusHours(hoursToAdd));
    }

    /**
     * Obtains new local time with specified number of minutes added.
     */
    public static LocalTime addMinutes(LocalTime localTime, int minutesToAdd) {
        checkArguments(localTime, minutesToAdd);
        return of(toJavaTime(localTime).plusMinutes(minutesToAdd));
    }

    /**
     * Obtains new local time with specified number of seconds added.
     */
    public static LocalTime addSeconds(LocalTime localTime, int secondsToAdd) {
        checkArguments(localTime, secondsToAdd);
        return of(toJavaTime(localTime).plusSeconds(secondsToAdd));
    }

    /**
     * Obtains new local time with specified number of nanoseconds added.
     */
    public static LocalTime addNanos(LocalTime localTime, long nanosToAdd) {
        checkArguments(localTime, nanosToAdd);
        return of(toJavaTime(localTime).plusNanos(nanosToAdd));
    }

    /**
     * Obtains a copy of this local time with the specified number of hours subtracted.
     */
    public static LocalTime subtractHours(LocalTime localTime, int hoursToSubtract) {
        checkArguments(localTime, hoursToSubtract);
        return of(toJavaTime(localTime).minusHours(hoursToSubtract));
    }

    /**
     * Obtains a copy of this local time with the specified number of minutes subtracted.
     */
    public static LocalTime subtractMinutes(LocalTime localTime, int minutesToSubtract) {
        checkArguments(localTime, minutesToSubtract);
        return of(toJavaTime(localTime).minusMinutes(minutesToSubtract));
    }

    /**
     * Obtains a copy of this local time with the specified number of seconds subtracted.
     */
    public static LocalTime subtractSeconds(LocalTime localTime, int secondsToSubtract) {
        checkArguments(localTime, secondsToSubtract);
        return of(toJavaTime(localTime).minusSeconds(secondsToSubtract));
    }

    /**
     * Obtains a copy of this local time with the specified number of nanoseconds subtracted.
     */
    public static LocalTime subtractNanos(LocalTime localTime, long nanosToSubtract) {
        checkArguments(localTime, nanosToSubtract);
        return of(toJavaTime(localTime).minusNanos(nanosToSubtract));
    }

    /**
     * Converts the passed time to string with optional part representing a fraction of a second.
     *
     * <p>Examples of results: {@code "13:45:30.123456789"}, {@code "09:37:00"}.
     */
    public static String toString(LocalTime time) {
        String result = toJavaTime(time).toString();
        return result;
    }

    /**
     * Parses the passed string into local time value.
     */
    public static LocalTime parse(String str) {
        java.time.LocalTime parsed = java.time.LocalTime.parse(str);
        return of(parsed);
    }

    /**
     * Arguments in preconditions checks for time modification routines.
     */
    enum Parameter {

        HOURS(HOURS_PER_DAY - 1),
        MINUTES(MINUTES_PER_HOUR - 1),
        SECONDS(SECONDS_PER_MINUTE - 1),
        MILLIS(MILLIS_PER_SECOND - 1),
        NANOS(NANOS_PER_SECOND - 1);

        private final int upperBound;

        Parameter(int bound) {
            upperBound = bound;
        }

        void check(int value) {
            DtPreconditions.checkBounds(value, name().toLowerCase(), 0, upperBound);
        }
    }
}
