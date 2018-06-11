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
import static io.spine.util.Exceptions.newIllegalArgumentException;
import static java.lang.String.format;

/**
 * Precondition routines specific to date/time.
 *
 * @author Alexander Yevsyukov
 */
class DtPreconditions {

    /** Prevent instantiation of this utility class. */
    private DtPreconditions() {
    }

    static void checkPositive(long value) {
        if (value <= 0) {
            throw newIllegalArgumentException("value (%d) must be positive", value);
        }
    }

    /**
     * Ensures that the passed value is not null and the delta value is positive.
     */
    static void checkArguments(Object value, int delta) {
        checkNotNull(value);
        checkPositive(delta);
    }

    /**
     * Ensures that target value is in between passed bounds.
     */
    public static void checkBounds(int value, String paramName, int lowBound, int highBound) {
        checkNotNull(paramName);
        if (!isBetween(value, lowBound, highBound)) {
            throw newIllegalArgumentException("%s (%d) should be in bounds [%d, %d] inclusive",
                                              paramName, value, lowBound, highBound);
        }
    }

    private static boolean isBetween(int value, int lowBound, int highBound) {
        return lowBound <= value && value <= highBound;
    }

    /**
     * Ensures that the passed date is valid.
     *
     * @throws IllegalArgumentException if
     * <ul>
     *     <li>the year is less or equal zero,
     *     <li>the month is {@code UNDEFINED},
     *     <li>the day is less or equal zero or greater than can be in the month.
     * </ul>
     */
    static void checkDate(int year, MonthOfYear month, int day) {
        checkPositive(year);
        checkNotNull(month);
        checkPositive(month.getNumber());
        checkPositive(day);

        final int daysInMonth = Months.daysInMonth(year, month);

        if (day > daysInMonth) {
            final String errMsg = format(
                    "A number of days cannot be more than %d, for this month and year.",
                    daysInMonth);
            throw new IllegalArgumentException(errMsg);
        }
    }
}
