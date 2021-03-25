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

import io.spine.time.string.TimeStringifiers;
import io.spine.util.SerializableConverter;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.LocalDates.checkDate;

/**
 * Utilities for working with {@code LocalDateTime}.
 */
public final class LocalDateTimes {

    /** Prevents instantiation of this utility class. */
    private LocalDateTimes() {
    }

    /**
     * Creates an instance based on the passed Java Time value.
     */
    public static LocalDateTime of(java.time.LocalDateTime value) {
        checkNotNull(value);
        return converter().convert(value);
    }

    /**
     * Creates an instance with the passed date and time.
     */
    public static LocalDateTime of(LocalDate date, LocalTime time) {
        checkNotNull(date);
        checkNotNull(time);
        checkDate(date);
        return create(date, time);
    }

    private static LocalDateTime create(LocalDate date, LocalTime time) {
        LocalDateTime result = LocalDateTime
                .newBuilder()
                .setDate(date)
                .setTime(time)
                .vBuild();
        return result;
    }

    /**
     * Converts the passed value to Java Time.
     *
     * @deprecated please use {@link LocalDateTime#toJavaTime()}
     */
    @Deprecated
    public static java.time.LocalDateTime toJavaTime(LocalDateTime value) {
        checkNotNull(value);
        return value.toJavaTime();
    }

    /**
     * Obtains converter from Java Time.
     */
    public static SerializableConverter<java.time.LocalDateTime, LocalDateTime> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Parses the date-time value from ISO-8601 format string.
     *
     * @see #toString(LocalDateTime)
     */
    public static LocalDateTime parse(String str) {
        checkNotNull(str);
        return TimeStringifiers.forLocalDateTime()
                               .reverse()
                               .convert(str);
    }

    /**
     * Converts a local date-time value into ISO-8601 format string.
     *
     * @see #parse(String)
     */
    public static String toString(LocalDateTime value) {
        checkNotNull(value);
        return TimeStringifiers.forLocalDateTime()
                               .convert(value);
    }

    /**
     * Converts from Java Time and back.
     */
    private static final class JtConverter
            extends AbstractConverter<java.time.LocalDateTime, LocalDateTime> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        private JtConverter() {
            super("LocalDateTimes.converter()");
        }

        @Override
        protected LocalDateTime doForward(java.time.LocalDateTime value) {
            LocalDate date = LocalDates.of(value.toLocalDate());
            LocalTime time = LocalTimes.of(value.toLocalTime());
            return create(date, time);
        }

        @Override
        protected java.time.LocalDateTime doBackward(LocalDateTime value) {
            java.time.LocalDate date = value.date().toJavaTime();
            java.time.LocalTime time = value.time().toJavaTime();
            return java.time.LocalDateTime.of(date, time);
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
