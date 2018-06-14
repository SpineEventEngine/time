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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Routines for working with {@link OffsetTime}.
 *
 * @author Alexander Aleksandrov
 * @author Alexander Yevsyukov
 */
public final class OffsetTimes {

    /** Prevent instantiation of this utility class. */
    private OffsetTimes() {
    }

    /**
     * Obtains offset time in the current time zone.
     */
    public static OffsetTime now() {
        return of(java.time.OffsetTime.now());
    }

    /**
     * Obtains offset time using {@code ZoneOffset}.
     */
    public static OffsetTime now(ZoneOffset zoneOffset) {
        checkNotNull(zoneOffset);
        java.time.ZoneOffset zo = ZoneOffsets.toJavaTime(zoneOffset);
        java.time.OffsetTime jt = java.time.LocalTime.now().atOffset(zo);

        LocalTime localTime = LocalTimes.of(jt.toLocalTime());
        
        OffsetTime result = OffsetTime
                .newBuilder()
                .setTime(localTime)
                .setOffset(zoneOffset)
                .build();
        return result;
    }

    /**
     * Obtains offset time using {@code LocalTime} and {@code ZoneOffset}.
     */
    public static OffsetTime of(LocalTime time, ZoneOffset zoneOffset) {
        checkNotNull(time);
        checkNotNull(zoneOffset);
        OffsetTime result = OffsetTime
                .newBuilder()
                .setTime(time)
                .setOffset(zoneOffset)
                .build();
        return result;
    }

    /**
     * Creates a new instance by passed Java Time value.
     */
    public static OffsetTime of(java.time.OffsetTime jt) {
        checkNotNull(jt);
        java.time.ZoneOffset zo = jt.getOffset();
        java.time.LocalTime lt = jt.toLocalTime();
        return of(LocalTimes.of(lt), ZoneOffsets.of(zo));
    }

    /**
     * Coverts the passed value to Java Time instance.
     */
    public static java.time.OffsetTime toJavaTime(OffsetTime value) {
        checkNotNull(value);
        java.time.OffsetTime result = java.time.OffsetTime.of(
                LocalTimes.toJavaTime(value.getTime()),
                ZoneOffsets.toJavaTime(value.getOffset())
        );
        return result;
    }

    /**
     * Returns a ISO 8601 time string corresponding to the passed value.
     */
    public static String toString(OffsetTime value) {
        checkNotNull(value);
        return toJavaTime(value).toString();
    }

    /**
     * Parse from ISO 8601 string to {@code OffsetTime}.
     */
    public static OffsetTime parse(String str) {
        java.time.OffsetTime parsed = java.time.OffsetTime.parse(str);
        return of(parsed);
    }
}
