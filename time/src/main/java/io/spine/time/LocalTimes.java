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

import com.google.common.base.Converter;
import io.spine.time.string.TimeStringifiers;

import java.time.DateTimeException;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.DtPreconditions.checkNotDefault;
import static io.spine.util.Exceptions.illegalArgumentWithCauseOf;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

/**
 * Routines for working with {@link LocalTime}.
 *
 * @author Alexander Aleksandrov
 * @author Alexander Yevsyukov
 */
public final class LocalTimes {

    /** Prevent instantiation of this utility class. */
    private LocalTimes() {
    }

    /**
     * Obtains current local time.
     */
    public static LocalTime now() {
        return of(java.time.LocalTime.now());
    }

    /**
     * Obtains local time from an hours, minutes, seconds, milliseconds, and nanoseconds.
     */
    public static LocalTime of(int hours, int minutes, int seconds, int nanos) {
        checkClockTime(hours, minutes, seconds, nanos);

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
        checkNotNull(value);
        return converter().convert(value);
    }

    /**
     * Converts the passed value to corresponding Java Time instance.
     */
    public static java.time.LocalTime toJavaTime(LocalTime value) {
        checkNotNull(value);
        return converter().reverse()
                          .convert(value);
    }

    static void checkTime(LocalTime time) {
        checkNotDefault(time);
        checkClockTime(time.getHour(),
                       time.getMinute(),
                       time.getSecond(),
                       time.getNano());
    }

    private static void checkClockTime(int hours, int minutes, int seconds, int nanos) {
        try {
            HOUR_OF_DAY.checkValidValue(hours);
            MINUTE_OF_HOUR.checkValidValue(minutes);
            SECOND_OF_MINUTE.checkValidValue(seconds);
            NANO_OF_SECOND.checkValidValue(nanos);
        } catch (DateTimeException e) {
            throw illegalArgumentWithCauseOf(e);
        }
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
     * Converts the passed time to string with optional part representing a fraction of a second.
     *
     * <p>Examples of results: {@code "13:45:30.123456789"}, {@code "09:37:00"}.
     *
     * @see #parse(String)
     */
    public static String toString(LocalTime time) {
        checkNotNull(time);
        return TimeStringifiers.forLocalTime()
                               .convert(time);
    }

    /**
     * Parses the passed string into local time value.
     *
     * @see #toString(LocalTime)
     */
    public static LocalTime parse(String str) {
        checkNotNull(str);
        return TimeStringifiers.forLocalTime()
                               .reverse()
                               .convert(str);
    }

    /**
     * Obtains the instance of Java Time converter.
     */
    public static Converter<java.time.LocalTime, LocalTime> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java Time and back.
     */
    private static final class JtConverter
            extends AbstractConverter<java.time.LocalTime, LocalTime> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        @Override
        protected LocalTime doForward(java.time.LocalTime value) {
            LocalTime result = LocalTime
                    .newBuilder()
                    .setHour(value.getHour())
                    .setMinute(value.getMinute())
                    .setSecond(value.getSecond())
                    .setNano(value.getNano())
                    .build();
            return result;
        }

        @Override
        protected java.time.LocalTime doBackward(LocalTime value) {
            java.time.LocalTime result = java.time.LocalTime
                    .of(value.getHour(),
                        value.getMinute(),
                        value.getSecond(),
                        value.getNano());
            return result;
        }

        @Override
        public String toString() {
            return "LocalTimes.converter()";
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
