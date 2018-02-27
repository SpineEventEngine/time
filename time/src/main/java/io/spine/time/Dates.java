/*
 * Copyright 2018, TeamDev Ltd. All rights reserved.
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
import static io.spine.validate.Validate.checkPositive;
import static java.lang.String.format;

/**
 * Utilities for working with calendar dates.
 * 
 * <p>A date is a reference to a particular day in some calendar system.  
 * For example, it is a combination of a year, a month and a day.
 *
 * @author Mykhailo Drachuk
 */
public class Dates {

    /** Prevent instantiation of this utility class. */
    private Dates() {
        // Does nothing.
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
    public static void checkDate(int year, MonthOfYear month, int day) {
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
