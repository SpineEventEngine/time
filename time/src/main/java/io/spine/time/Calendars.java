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

import java.util.Calendar;
import java.util.TimeZone;

import static io.spine.time.SiTime.MILLIS_PER_SECOND;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.ZONE_OFFSET;
import static java.util.Calendar.getInstance;

/**
 * Utilities for working with {@link Calendar}.
 *
 * <p> This utility class is needed while Spine is based on Java 7.
 * Java 8 introduces new date/time API in the package {@code java.time}.
 * Spine v2 will be based on Java 8 and this class will be deprecated.
 *
 * @author Alexander Aleksandrov
 */
@SuppressWarnings({"ClassWithTooManyMethods" /* OK for this utility class. */,
                   "MagicConstant" /* We use conversion methods instead. */})
final class Calendars {

    /** Prevent instantiation of this utility class. */
    private Calendars() {
    }

    /**
     * Obtains a zone offset of the passed {@code Calendar}.
     *
     * @return zone offset in seconds
     */
    static int getZoneOffset(Calendar cal) {
        int zoneOffset = cal.get(ZONE_OFFSET) / MILLIS_PER_SECOND;
        return zoneOffset;
    }

    /**
     * Obtains hours using {@code Calendar}.
     */
    static int getHours(Calendar cal) {
        int hours = cal.get(HOUR_OF_DAY);
        return hours;
    }

    /**
     * Obtains minutes using {@code Calendar}.
     */
    static int getMinutes(Calendar cal) {
        int minutes = cal.get(MINUTE);
        return minutes;
    }

    /**
     * Obtains seconds using {@code Calendar}.
     */
    static int getSeconds(Calendar cal) {
        int seconds = cal.get(SECOND);
        return seconds;
    }

    /**
     * Obtains calendar at the specified zone offset
     *
     * @param zoneOffset time offset for specified zone
     * @return new {@code Calendar} instance at specific zone offset
     */
    static Calendar at(ZoneOffset zoneOffset) {
        TimeZone timeZone = ZoneConverter.getInstance()
                                         .reverse()
                                         .convert(zoneOffset);
        Calendar result = getInstance(timeZone);
        return result;
    }
}
