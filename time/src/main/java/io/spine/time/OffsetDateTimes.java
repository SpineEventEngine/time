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
import io.spine.time.string.TimeStringifiers;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Routines for working with {@link OffsetDateTime}.
 *
 * @author Alexander Aleksandrov
 * @author Alexander Yevsyukov
 */
public final class OffsetDateTimes {

    /** Prevent instantiation of this utility class. */
    private OffsetDateTimes() {
    }

    /**
     * Obtains current date/time at the system time zone.
     */
    public static OffsetDateTime now() {
        ZoneOffset offset = ZoneOffsets.getDefault();
        return now(offset);
    }

    /**
     * Obtains current date-time at the the passed time zone.
     */
    public static OffsetDateTime now(ZoneOffset offset) {
        java.time.ZoneOffset zo = ZoneOffsets.toJavaTime(offset);
        java.time.OffsetDateTime now = java.time.OffsetDateTime.now(zo);
        LocalTime localTime = LocalTimes.of(now.toLocalTime());
        LocalDate localDate = LocalDates.of(now.toLocalDate());
        return create(localDate, localTime, offset);
    }

    /**
     * Creates a new instance with the passed values.
     */
    public static OffsetDateTime of(LocalDate date, LocalTime time, ZoneOffset offset) {
        checkNotNull(date);
        checkNotNull(time);
        checkNotNull(offset);
        return create(date, time, offset);
    }

    /**
     * Creates new instance based on the passed Java Time value.
     */
    public static OffsetDateTime of(java.time.OffsetDateTime value) {
        checkNotNull(value);
        return converter().convert(value);
    }

    private static OffsetDateTime create(LocalDate date, LocalTime time, ZoneOffset offset) {
        OffsetDateTime.Builder result = OffsetDateTime
                .newBuilder()
                .setDateTime(LocalDateTimes.of(date, time))
                .setOffset(offset);
        return result.build();
    }

    /**
     * Converts the passed value to Java Time instance.
     */
    public static java.time.OffsetDateTime toJavaTime(OffsetDateTime value) {
        checkNotNull(value);
        return converter().reverse()
                          .convert(value);
    }

    /**
     * Returns a ISO-8601 date/time string corresponding to the passed value.
     */
    public static String toString(OffsetDateTime value) {
        checkNotNull(value);
        return TimeStringifiers.forOffsetDateTime()
                               .convert(value);
    }

    /**
     * Parses from ISO-8601 date/time string to {@code OffsetDateTime}.
     */
    public static OffsetDateTime parse(String value) {
        checkNotNull(value);
        return TimeStringifiers.forOffsetDateTime()
                               .reverse()
                               .convert(value);
    }

    /**
     * Obtains converter from Java Time and back.
     */
    public static Converter<java.time.OffsetDateTime, OffsetDateTime> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java Time and back.
     */
    private static final class JtConverter
            extends AbstractConverter<java.time.OffsetDateTime, OffsetDateTime> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        @Override
        protected OffsetDateTime doForward(java.time.OffsetDateTime value) {
            java.time.LocalDate ld = value.toLocalDate();
            java.time.LocalTime lt = value.toLocalTime();
            java.time.ZoneOffset zo = value.toZonedDateTime().getOffset();
            return create(LocalDates.of(ld),
                          LocalTimes.of(lt),
                          ZoneOffsets.of(zo));
        }

        @Override
        protected java.time.OffsetDateTime doBackward(OffsetDateTime value) {
            java.time.OffsetDateTime result = java.time.OffsetDateTime.of(
                    LocalDateTimes.toJavaTime(value.getDateTime()),
                    ZoneOffsets.toJavaTime(value.getOffset())
            );
            return result;
        }

        @Override
        public String toString() {
            return "OffsetDateTimes.converter()";
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
