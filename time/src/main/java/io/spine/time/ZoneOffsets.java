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
import io.spine.time.Formats.Parameter;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.util.TimeZone;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static io.spine.time.Durations2.hoursAndMinutes;
import static io.spine.time.Formats.formatOffsetTime;
import static io.spine.time.EarthTime.MINUTES_PER_HOUR;
import static io.spine.time.EarthTime.SECONDS_PER_MINUTE;
import static io.spine.validate.Validate.checkBounds;
import static java.lang.String.format;

/**
 * Utilities for working with {@code ZoneOffset}s.
 *
 * @author Alexander Yevsyukov
 * @author Alexander Aleksandrov
 * @see ZoneOffset
 */
public final class ZoneOffsets {

    public static final int MIN_HOURS_OFFSET = -11;
    public static final int MAX_HOURS_OFFSET = 14;

    public static final int MIN_MINUTES_OFFSET = 0;
    public static final int MAX_MINUTES_OFFSET = 60;

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
        checkHourOffset(hours, false);

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
        checkHourOffset(hours, true);
        checkMinuteOffset(minutes);
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

    private static void checkHourOffset(int hours, boolean assumingMinutes) {
        // If the offset contains minutes too, we make the range smaller by one hour from each end.
        int shift = (assumingMinutes ? 1 : 0);
        checkBounds(hours, Parameter.hours.name(),
                    MIN_HOURS_OFFSET + shift,
                    MAX_HOURS_OFFSET - shift);
    }

    private static void checkMinuteOffset(int minutes) {
        checkBounds(Math.abs(minutes), Parameter.minutes.name(),
                    MIN_MINUTES_OFFSET, MAX_MINUTES_OFFSET);
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
}
