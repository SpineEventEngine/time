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

import io.spine.string.Stringifier;
import io.spine.time.string.TimeStringifiers;
import io.spine.util.SerializableConverter;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

/**
 * Routines for working with {@link LocalTime}.
 */
public final class LocalTimes {

    /** Prevent instantiation of this utility class. */
    private LocalTimes() {
    }

    /**
     * Obtains local time from an hours, minutes, seconds, milliseconds, and nanoseconds.
     */
    public static LocalTime of(int hours, int minutes, int seconds, int nanos) {
        var result = LocalTime.newBuilder()
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
        var result = converter().convert(value);
        return requireNonNull(result);
    }

    /**
     * Converts the passed value to corresponding Java Time instance.
     *
     * @deprecated please use {@link LocalTime#toJavaTime()}
     */
    @Deprecated
    public static java.time.LocalTime toJavaTime(LocalTime value) {
        checkNotNull(value);
        var result = converter().reverse().convert(value);
        return requireNonNull(result);
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
        var result = stringifier().convert(time);
        return requireNonNull(result);
    }

    /**
     * Parses the passed string into local time value.
     *
     * @see #toString(LocalTime)
     */
    public static LocalTime parse(String str) {
        checkNotNull(str);
        var result = stringifier().reverse().convert(str);
        return requireNonNull(result);
    }

    private static Stringifier<LocalTime> stringifier() {
        return TimeStringifiers.forLocalTime();
    }

    /**
     * Obtains the instance of Java Time converter.
     */
    public static SerializableConverter<java.time.LocalTime, LocalTime> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java Time and back.
     */
    private static final class JtConverter
            extends AbstractConverter<java.time.LocalTime, LocalTime> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        private JtConverter() {
            super("LocalTimes.converter()");
        }

        @Override
        protected LocalTime doForward(java.time.LocalTime value) {
            var result = LocalTime.newBuilder()
                    .setHour(value.getHour())
                    .setMinute(value.getMinute())
                    .setSecond(value.getSecond())
                    .setNano(value.getNano())
                    .build();
            return result;
        }

        @Override
        protected java.time.LocalTime doBackward(LocalTime value) {
            var result = java.time.LocalTime
                    .of(value.getHour(),
                        value.getMinute(),
                        value.getSecond(),
                        value.getNano());
            return result;
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
