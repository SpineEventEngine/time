/*
 * Copyright 2019, TeamDev. All rights reserved.
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

import java.time.DateTimeException;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.util.Exceptions.illegalArgumentWithCauseOf;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;

/**
 * Utilities for working with {@link io.spine.time.DayOfWeek DayOfWeek} instances.
 */
public final class DaysOfWeek {

    /** Prevents instantiation of this utility class. */
    private DaysOfWeek() {
    }

    /**
     * Obtains today's day of week.
     */
    public static DayOfWeek now() {
        return of(java.time.LocalDate.now()
                                     .getDayOfWeek());
    }

    /**
     * Obtains the week day corresponding to the passed Java Time value.
     */
    public static DayOfWeek of(java.time.DayOfWeek day) {
        checkNotNull(day);
        DayOfWeek result = converter().convert(day);
        return result;
    }

    private static void checkDay(int day) {
        try {
            DAY_OF_WEEK.checkValidValue(day);
        } catch (DateTimeException e) {
            throw illegalArgumentWithCauseOf(e);
        }
    }

    /**
     * Converts the passed instance to Java Time value.
     */
    public static java.time.DayOfWeek toJavaTime(DayOfWeek day) {
        checkNotNull(day);
        checkDay(day.getNumber());
        java.time.DayOfWeek result = converter().reverse()
                                                .convert(day);
        return result;
    }

    /**
     * Obtains string representation of the passed day of week.
     *
     * <p>Returned string is an internal representation, and should not be used
     * in the user interface.
     *
     * <p>For displaying a day of week, please use
     * {@link java.time.DayOfWeek#getDisplayName(java.time.format.TextStyle, java.util.Locale)
     *  java.time.DayOfWeek.getDisplayName(TextStyle, Locale)}.
     *
     * @see #parse(String)
     */
    public static String toString(DayOfWeek value) {
        checkNotNull(value);
        return TimeStringifiers.forDayOfWeek()
                               .convert(value);
    }

    /**
     * Parses a day of week value from an internal representation string.
     *
     * @see #toString(DayOfWeek)
     */
    public static DayOfWeek parse(String str) {
        checkNotNull(str);
        return TimeStringifiers.forDayOfWeek()
                               .reverse()
                               .convert(str);
    }

    /**
     * Obtains the converter from Java Time.
     */
    public static Converter<java.time.DayOfWeek, DayOfWeek> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java Time and back.
     */
    private static final class JtConverter
            extends AbstractConverter<java.time.DayOfWeek, DayOfWeek> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        private JtConverter() {
            super("DaysOfWeek.converter()");
        }

        @Override
        protected DayOfWeek doForward(java.time.DayOfWeek day) {
            DayOfWeek result = DayOfWeek.forNumber(day.getValue());
            return result;
        }

        @Override
        protected java.time.DayOfWeek doBackward(DayOfWeek day) {
            java.time.DayOfWeek result = java.time.DayOfWeek.of(day.getNumber());
            return result;
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
