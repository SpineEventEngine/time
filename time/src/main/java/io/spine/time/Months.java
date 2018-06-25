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
import static io.spine.time.DtPreconditions.checkBounds;

/**
 * Utilities for working with calendar months.
 *
 * @author Mykhailo Drachuk
 * @author Alexander Yevsyukov
 */
public class Months {

    private static final String MONTH_PARAM = Month.class.getName()
                                                         .toLowerCase();

    /** Prevent instantiation of this utility class. */
    private Months() {
    }

    static void checkMonth(int month) {
        checkBounds(month, MONTH_PARAM,
                    Month.JANUARY.getNumber(),
                    Month.DECEMBER.getNumber());
    }

    /**
     * Creates an instance by the passed number.
     */
    public static Month of(int month) {
        checkMonth(month);
        return Month.forNumber(month);
    }

    /**
     * Obtains the month of the passed date.
     */
    public static Month of(java.time.LocalDate date) {
        checkNotNull(date);
        return converter().convert(date.getMonth());
    }

    /**
     * Converts the passed Java Time value.
     */
    public static Month of(java.time.Month month) {
        checkNotNull(month);
        return converter().convert(month);
    }

    /**
     * Converts the passed instance to the Java Time value.
     */
    public static java.time.Month toJavaTime(Month value) {
        checkNotNull(value);
        return converter().reverse()
                          .convert(value);
    }

    /**
     * Obtains the instance of Java Time converter.
     */
    public static Converter<java.time.Month, Month> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java Time and back.
     */
    private static class JtConverter extends AbstractConverter<java.time.Month, Month> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        @Override
        protected Month doForward(java.time.Month month) {
            Month result = Month.forNumber(month.getValue());
            return result;
        }

        @Override
        protected java.time.Month doBackward(Month month) {
            return java.time.Month.of(month.getNumber());
        }

        @Override
        public String toString() {
            return "Months.converter()";
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
