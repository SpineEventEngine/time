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

/**
 * Utilities for working with calendar months.
 *
 * @author Mykhailo Drachuk
 */
public class Months {

    private static final int FEBRUARY_MIN = 28;

    /** Prevent instantiation of this utility class. */
    private Months() {
        // Does nothing.
    }

    /**
     * Obtains a number of days in the passed month of the year.
     */
    public static int daysInMonth(int year, MonthOfYear month) {
        final int monthNumber = month.getNumber();
        final int days;
        if (Years.isLeapYear(year) && monthNumber == 2) {
            return FEBRUARY_MIN + 1;
        }
        days = FEBRUARY_MIN + ((0x3bbeecc >> (monthNumber * 2)) & 3);
        return days;
    }
}
