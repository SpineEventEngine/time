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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utilities for working with {@code LocalDateTime}.
 *
 * @author Alexander Yevsyukov
 */
public class LocalDateTimes {

    /** Prevents instantiation of this utility class. */
    private LocalDateTimes() {
    }

    /**
     * Obtains current date-time.
     */
    public static LocalDateTime now() {
        return of(java.time.LocalDateTime.now());
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
        return create(date, time);
    }

    private static LocalDateTime create(LocalDate date, LocalTime time) {
        LocalDateTime.Builder result = LocalDateTime
                .newBuilder()
                .setDate(date)
                .setTime(time);
        return result.build();
    }

    /**
     * Converts the passed value to Java Time.
     */
    public static java.time.LocalDateTime toJavaTime(LocalDateTime value) {
        checkNotNull(value);
        return converter().reverse()
                          .convert(value);
    }

    /**
     * Obtains converter from Java Time.
     */
    public static Converter<java.time.LocalDateTime, LocalDateTime> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java Time and back.
     */
    private static class JtConverter
            extends AbstractConverter<java.time.LocalDateTime, LocalDateTime> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        @Override
        protected LocalDateTime doForward(java.time.LocalDateTime value) {
            LocalDate date = LocalDates.of(value.toLocalDate());
            LocalTime time = LocalTimes.of(value.toLocalTime());
            return create(date, time);
        }

        @Override
        protected java.time.LocalDateTime doBackward(LocalDateTime value) {
            java.time.LocalDate date = LocalDates.toJavaTime(value.getDate());
            java.time.LocalTime time = LocalTimes.toJavaTime(value.getTime());
            return java.time.LocalDateTime.of(date, time);
        }

        @Override
        public String toString() {
            return "LocalDateTimes.converter()";
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}