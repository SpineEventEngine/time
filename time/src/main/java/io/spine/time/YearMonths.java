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

/**
 * Utilities for working with {@code YearMonth} values.
 *
 * @author Alexander Yevsyukov
 */
public class YearMonths {

    /** Prevents instantiation of this utility class. */
    private YearMonths() {
    }

    /**
     * Obtains current month.
     */
    public static YearMonth now() {
        return of(java.time.YearMonth.now());
    }

    /**
     * Creates an instance with the the passed year and month.
     */
    public static YearMonth of(int year, int month) {
        return create(year, month);
    }

    private static YearMonth create(int year, int month) {
        Months.checkMonth(month);
        YearMonth.Builder result = YearMonth
                .newBuilder()
                .setYear(year)
                .setMonth(Months.of(month));
        return result.build();
    }

    /**
     * Converts the passed Java Time value.
     */
    public static YearMonth of(java.time.YearMonth value) {
        checkNotNull(value);
        return converter().convert(value);
    }

    /**
     * Converts the passed value to Java Time.
     */
    public static java.time.YearMonth toJavaTime(YearMonth value) {
        checkNotNull(value);
        return converter().reverse()
                          .convert(value);
    }

    /**
     * Obtains the converter from Java Time and back.
     */
    public static Converter<java.time.YearMonth, YearMonth> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java Time and back.
     */
    private static final class JtConverter
            extends AbstractConverter<java.time.YearMonth, YearMonth> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        @Override
        protected YearMonth doForward(java.time.YearMonth value) {
            YearMonth result = create(value.getYear(), value.getMonthValue());
            return result;
        }

        @Override
        protected java.time.YearMonth doBackward(YearMonth value) {
            java.time.YearMonth result = java.time.YearMonth
                    .of(value.getYear(), value.getMonthValue());
            return result;
        }

        @Override
        public String toString() {
            return "YearMonths.converter()";
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
