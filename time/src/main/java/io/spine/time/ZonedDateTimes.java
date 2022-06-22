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

import io.spine.time.string.TimeStringifiers;
import io.spine.util.SerializableConverter;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.DtPreconditions.checkNotDefault;

/**
 * Utilities for working with {@code ZonedDateTime}.
 */
public final class ZonedDateTimes {

    /** Prevents instantiation of this utility class. */
    private ZonedDateTimes() {
    }

    /**
     * Obtains the zoned time for the local date-time at the passed time zone.
     */
    public static ZonedDateTime of(LocalDateTime dateTime, ZoneId zone) {
        checkNotDefault(dateTime);
        checkNotDefault(zone);
        return create(dateTime, zone);
    }

    private static ZonedDateTime create(LocalDateTime dateTime, ZoneId zone) {
        ZonedDateTime result = ZonedDateTime
                .newBuilder()
                .setDateTime(dateTime)
                .setZone(zone)
                .vBuild();
        return result;
    }

    /**
     * Creates the instance by the passed Java Time value.
     */
    public static ZonedDateTime of(java.time.ZonedDateTime value) {
        checkNotNull(value);
        return converter().convert(value);
    }

    /**
     * Converts the passed value to Java Time.
     */
    public static java.time.ZonedDateTime toJavaTime(ZonedDateTime value) {
        checkNotDefault(value);
        return converter().reverse()
                          .convert(value);
    }

    /**
     * Converts the passed value to ISO-8601 zoned date/time string.
     */
    public static String toString(ZonedDateTime value) {
        checkNotNull(value);
        return TimeStringifiers.forZonedDateTime()
                               .convert(value);
    }

    /**
     * Parses the ISO-8601 string representation of the zoned date-time value.
     */
    public static ZonedDateTime parse(String str) {
        checkNotNull(str);
        return TimeStringifiers.forZonedDateTime()
                               .reverse()
                               .convert(str);
    }

    /**
     * Obtains converter from Java Time and back.
     */
    public static SerializableConverter<java.time.ZonedDateTime, ZonedDateTime> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java Time and back.
     */
    private static final class JtConverter
            extends AbstractConverter<java.time.ZonedDateTime, ZonedDateTime> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        private JtConverter() {
            super("ZonedDateTimes.converter()");
        }

        @Override
        protected ZonedDateTime doForward(java.time.ZonedDateTime value) {
            LocalDateTime dateTime = LocalDateTimes.of(value.toLocalDateTime());
            ZoneId zoneId = ZoneIds.of(value.getZone());
            return create(dateTime, zoneId);
        }

        @Override
        protected java.time.ZonedDateTime doBackward(ZonedDateTime value) {
            java.time.LocalDateTime dateTime = LocalDateTimes.toJavaTime(value.getDateTime());
            java.time.ZoneId zoneId = ZoneIds.toJavaTime(value.getZone());
            return java.time.ZonedDateTime.of(dateTime, zoneId);
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
