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

/**
 * Routines for working with {@link OffsetTime}.
 *
 * @deprecated please use {@link LocalTime} in combination with {@link ZoneId}.
 */
@Deprecated
public final class OffsetTimes {

    /** Prevent instantiation of this utility class. */
    private OffsetTimes() {
    }

    /**
     * Obtains offset time using {@code LocalTime} and {@code ZoneOffset}.
     */
    public static OffsetTime of(LocalTime time, ZoneOffset zoneOffset) {
        checkNotNull(time);
        checkNotNull(zoneOffset);
        return create(time, zoneOffset);
    }

    private static OffsetTime create(LocalTime time, ZoneOffset zoneOffset) {
        OffsetTime result = OffsetTime
                .newBuilder()
                .setTime(time)
                .setOffset(zoneOffset)
                .vBuild();
        return result;
    }

    /**
     * Creates a new instance by passed Java Time value.
     */
    public static OffsetTime of(java.time.OffsetTime value) {
        checkNotNull(value);
        return converter().convert(value);
    }

    /**
     * Coverts the passed value to Java Time instance.
     */
    public static java.time.OffsetTime toJavaTime(OffsetTime value) {
        checkNotNull(value);
        return converter().reverse()
                          .convert(value);
    }

    /**
     * Returns a ISO-8601 time string corresponding to the passed value.
     *
     * @deprecated please use {@link java.time.OffsetTime#toString()} instead.
     */
    @Deprecated
    public static String toString(OffsetTime value) {
        checkNotNull(value);
        return TimeStringifiers.forOffsetTime()
                               .convert(value);
    }

    /**
     * Parse from ISO-8601 string to {@code OffsetTime}.
     *
     * @deprecated please use {@link java.time.OffsetTime#parse(CharSequence)} instead.
     */
    @Deprecated
    public static OffsetTime parse(String str) {
        checkNotNull(str);
        return TimeStringifiers.forOffsetTime()
                               .reverse()
                               .convert(str);
    }

    /**
     * Obtains converter from Java Time.
     */
    public static SerializableConverter<java.time.OffsetTime, OffsetTime> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java Time and back.
     */
    private static final class JtConverter
            extends AbstractConverter<java.time.OffsetTime, OffsetTime> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        private JtConverter() {
            super("OffsetTimes.converter()");
        }

        @Override
        protected OffsetTime doForward(java.time.OffsetTime value) {
            java.time.LocalTime lt = value.toLocalTime();
            java.time.ZoneOffset zo = value.getOffset();
            return of(LocalTimes.of(lt), ZoneOffsets.of(zo));
        }

        @Override
        protected java.time.OffsetTime doBackward(OffsetTime value) {
            java.time.OffsetTime result = java.time.OffsetTime.of(
                    LocalTimes.toJavaTime(value.getTime()),
                    ZoneOffsets.toJavaTime(value.getOffset())
            );
            return result;
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
