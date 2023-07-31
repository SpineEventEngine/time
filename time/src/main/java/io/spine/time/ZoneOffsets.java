/*
 * Copyright 2023, TeamDev. All rights reserved.
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

import com.google.protobuf.Duration;
import com.google.protobuf.util.Durations;
import io.spine.time.string.TimeStringifiers;
import io.spine.util.SerializableConverter;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.TimeZone;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.protobuf.util.Durations.fromHours;
import static io.spine.protobuf.Durations2.hoursAndMinutes;
import static io.spine.util.Exceptions.unsupported;
import static java.util.Objects.requireNonNull;

/**
 * Utilities for working with {@code ZoneOffset}s.
 *
 * @see ZoneOffset
 * @deprecated please use {@link ZoneIds} instead.
 */
@Deprecated
public final class ZoneOffsets {

    private static final ZoneOffset UTC = ZoneOffset.newBuilder()
            .setAmountSeconds(0)
            .build();

    /** Prevent instantiation of this utility class. */
    private ZoneOffsets() {
    }

    /**
     * Obtains the UTC offset.
     */
    public static ZoneOffset utc() {
        return UTC;
    }

    /**
     * Obtains a {@code ZoneOffset} instance using default {@code TimeZone} of the Java
     * virtual machine.
     *
     * @see TimeZone#getDefault()
     */
    public static ZoneOffset getDefault() {
        @SuppressWarnings("JavaTimeDefaultTimeZone")
        var zo = java.time.OffsetTime.now().getOffset();
        return of(zo);
    }

    /**
     * Converts the passed instance to the Java Time value.
     */
    public static java.time.ZoneOffset toJavaTime(ZoneOffset value) {
        checkNotNull(value);
        var result = converter().reverse().convert(value);
        return requireNonNull(result);
    }

    /**
     * Converts Java Time value to {@code ZoneOffset}.
     */
    public static ZoneOffset of(java.time.ZoneOffset zo) {
        checkNotNull(zo);
        var result = converter().convert(zo);
        return requireNonNull(result);
    }

    /**
     * Obtains the ZoneOffset instance using an offset in hours.
     */
    public static ZoneOffset ofHours(int hours) {
        Parameter.HOURS.check(hours);

        var hourDuration = fromHours(hours);
        var seconds = toSeconds(hourDuration);
        return ofSeconds(seconds);
    }

    /**
     * Obtains the ZoneOffset for the passed number of seconds.
     *
     * <p>If zero is passed {@link #utc()} instance is returned.
     *
     * @param seconds
     *         a positive, zero
     * @return the instance for the passed offset
     */
    public static ZoneOffset ofSeconds(int seconds) {
        return create(seconds, null);
    }

    /**
     * Obtains the ZoneOffset instance using an offset in hours and minutes.
     *
     * <p>If a negative zone offset is created both passed values must be negative.
     */
    public static ZoneOffset ofHoursMinutes(int hours, int minutes) {
        Parameter.HOURS.checkReduced(hours);
        Parameter.MINUTES.check(minutes);
        checkArgument(((hours < 0) == (minutes < 0)) || (minutes == 0),
                      "Hours (%s) and minutes (%s) must have the same sign.", hours, minutes);

        var duration = hoursAndMinutes(hours, minutes);
        var seconds = toSeconds(duration);
        return ofSeconds(seconds);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    // It is safe, as we check bounds of the arguments when creating Durations.
    private static int toSeconds(Duration duration) {
        return (int) Durations.toSeconds(duration);
    }

    /**
     * Parses the time zone offset value formatted as a signed value of hours and minutes.
     *
     * <p>Examples of accepted values: {@code +0300}, {@code -04:30}.
     *
     * @deprecated please use {@link java.time.ZoneOffset#of(String)} instead
     */
    @Deprecated
    public static ZoneOffset parse(String value) {
        checkNotNull(value);
        var converter = TimeStringifiers.forZoneOffset().reverse();
        var result = converter.convert(value);
        return requireNonNull(result);
    }

    /**
     * Converts the passed zone offset into a string with a signed amount of hours and minutes.
     *
     * @deprecated please use {@link java.time.ZoneOffset#toString()} instead.
     */
    @Deprecated
    public static String toString(ZoneOffset zoneOffset) {
        checkNotNull(zoneOffset);
        var result = TimeStringifiers.forZoneOffset().convert(zoneOffset);
        return requireNonNull(result);
    }

    private static ZoneOffset create(int offsetInSeconds, @Nullable String zoneId) {
        if (offsetInSeconds == 0 && zoneId == null) {
            return utc();
        }

        return ZoneOffset.newBuilder()
                         .setAmountSeconds(offsetInSeconds)
                         .build();
    }

    /**
     * Obtains converter from Java Time and back.
     */
    public static SerializableConverter<java.time.ZoneOffset, ZoneOffset> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Parameter checks for zone offset values.
     */
    enum Parameter {

        HOURS(-18, 18) {
            @Override
            void check(int value) {
                checkBounds(value);
            }

            /**
             * Checks the hour value of an offset that contains minutes.
             *
             * <p>If the offset contains minutes too, we make the range smaller by one hour from
             * each end.
             */
            @Override
            void checkReduced(int value) {
                DtPreconditions.checkBounds(value, name().toLowerCase(Locale.ENGLISH), min() + 1,
                                            max() - 1);
            }
        },

        MINUTES(0, 60) {
            @Override
            void check(int value) {
                checkBounds(Math.abs(value));
            }

            /**
             * Always throws exception since minute offset parameters do not support
             * reduced check.
             */
            @Override
            void checkReduced(int value) {
                throw unsupported();
            }
        };

        private final int min;
        private final int max;

        Parameter(int min, int max) {
            this.min = min;
            this.max = max;
        }

        abstract void check(int value);

        abstract void checkReduced(int value);

        protected void checkBounds(int value) {
            DtPreconditions.checkBounds(value, name().toLowerCase(Locale.ENGLISH), min(), max());
        }

        int min() {
            return min;
        }

        int max() {
            return max;
        }
    }

    /**
     * Converts from Java Time and back.
     */
    private static final class JtConverter
            extends AbstractConverter<java.time.ZoneOffset, ZoneOffset> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        private JtConverter() {
            super("ZoneOffsets.converter()");
        }

        @Override
        protected ZoneOffset doForward(java.time.ZoneOffset value) {
            return ofSeconds(value.getTotalSeconds());
        }

        @Override
        protected java.time.ZoneOffset doBackward(ZoneOffset value) {
            var result = java.time.ZoneOffset
                    .ofTotalSeconds(value.getAmountSeconds());
            return result;
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
