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

import com.google.protobuf.Duration;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.util.TimeZone;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static io.spine.time.Durations2.hoursAndMinutes;
import static io.spine.time.EarthTime.MINUTES_PER_HOUR;
import static io.spine.time.EarthTime.SECONDS_PER_MINUTE;
import static io.spine.time.Formats.formatOffsetTime;
import static io.spine.util.Exceptions.unsupported;
import static java.lang.String.format;

/**
 * Utilities for working with {@code ZoneOffset}s.
 *
 * @author Alexander Yevsyukov
 * @author Alexander Aleksandrov
 * @see ZoneOffset
 */
public final class ZoneOffsets {

    public static final ZoneOffset UTC =
            ZoneOffset.newBuilder()
                      .setId(ZoneId.newBuilder()
                                   .setValue("UTC"))
                      .setAmountSeconds(0)
                      .build();

    /** Prevent instantiation of this utility class. */
    private ZoneOffsets() {
    }

    /**
     * Obtains a {@code ZoneOffset} instance using default {@code TimeZone} of the Java
     * virtual machine.
     *
     * @see TimeZone#getDefault()
     */
    public static ZoneOffset getDefault() {
        TimeZone timeZone = TimeZone.getDefault();
        ZoneOffset result = ZoneConverter.getInstance()
                                         .convert(timeZone);
        return result;
    }

    public static java.time.ZoneOffset toJavaTime(ZoneOffset value) {
        java.time.ZoneOffset result = java.time.ZoneOffset.ofTotalSeconds(value.getAmountSeconds());
        return result;
    }

    /**
     * Obtains the ZoneOffset instance using an offset in hours.
     */
    public static ZoneOffset ofHours(int hours) {
        Parameter.HOURS.check(hours);

        Duration hourDuration = Durations2.fromHours(hours);
        int seconds = toSeconds(hourDuration);
        return ofSeconds(seconds);
    }

    /**
     * Obtains the ZoneOffset for the passed number of seconds.
     *
     * <p>If zero is passed {@link #UTC} instance is returned.
     *
     * @param seconds a positive, zero,
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

        Duration duration = hoursAndMinutes(hours, minutes);
        int seconds = toSeconds(duration);
        return ofSeconds(seconds);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    // It is safe, as we check bounds of the arguments.
    private static int toSeconds(Duration duration) {
        return (int) Durations2.toSeconds(duration);
    }

    /**
     * Parses the time zone offset value formatted as a signed value of hours and minutes.
     *
     * <p>Examples of accepted values: {@code +3:00}, {@code -04:30}.
     *
     * @throws ParseException if the passed value has invalid format
     */
    public static ZoneOffset parse(String value) throws ParseException {
        int pos = value.indexOf(':');
        if (pos == -1) {
            final String errMsg = format("Invalid offset value: \"%s\"", value);
            throw new ParseException(errMsg, 0);
        }
        char signChar = value.charAt(0);
        boolean positive = signChar == Formats.PLUS;
        boolean negative = signChar == Formats.MINUS;

        if (!(positive || negative)) {
            String errMsg = format("Missing sign char in offset value: \"%s\"", value);
            throw new ParseException(errMsg, 0);
        }

        String hoursStr = value.substring(1, pos);
        String minutesStr = value.substring(pos + 1);
        long hours = Long.parseLong(hoursStr);
        long minutes = Long.parseLong(minutesStr);
        long totalMinutes = hours * MINUTES_PER_HOUR + minutes;
        long seconds = totalMinutes * SECONDS_PER_MINUTE;

        if (negative) {
            seconds = -seconds;
        }

        @SuppressWarnings("NumericCastThatLosesPrecision") // OK since the value cannot grow larger.
        ZoneOffset result = ofSeconds((int) seconds);
        return result;
    }

    /**
     * Converts the passed zone offset into a string with a signed amount of hours and minutes.
     */
    public static String toString(ZoneOffset zoneOffset) {
        checkNotNull(zoneOffset);
        long seconds = zoneOffset.getAmountSeconds();
        long totalMinutes = seconds / SECONDS_PER_MINUTE;
        long hours = totalMinutes / MINUTES_PER_HOUR;
        long minutes = totalMinutes % MINUTES_PER_HOUR;
        StringBuilder builder = new StringBuilder(6)
                .append(seconds >= 0 ? Formats.PLUS : Formats.MINUS)
                .append(formatOffsetTime(hours, minutes));
        return builder.toString();
    }

    static ZoneOffset create(int offsetInSeconds, @Nullable String zoneId) {
        if (offsetInSeconds == 0 && zoneId == null) {
            return UTC;
        }
        String id = nullToEmpty(zoneId);
        return ZoneOffset.newBuilder()
                         .setAmountSeconds(offsetInSeconds)
                         .setId(ZoneId.newBuilder().setValue(id))
                         .build();
    }

    /**
     * Verifies if the passed instance has zero offset and no ID.
     *
     * <p>If the passed instance is zero without a zone ID, returns the {@link #UTC} instance.
     * Otherwise returns the passed instance.
     */
    static ZoneOffset adjustZero(ZoneOffset offset) {
        boolean noZoneId = offset.getId()
                                 .getValue()
                                 .isEmpty();
        if (offset.getAmountSeconds() == 0 && noZoneId) {
            return UTC;
        }
        return offset;
    }

    /**
     * Parameter checks for zone offset values.
     */
    enum Parameter {

        HOURS(-11, 14) {

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
                DtPreconditions.checkBounds(value, name().toLowerCase(), min() + 1, max() - 1);
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
            DtPreconditions.checkBounds(value, name().toLowerCase(), min(), max());
        }

        int min() {
            return min;
        }

        int max() {
            return max;
        }
    }
}
