/*
 * Copyright 2021, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

import com.google.common.testing.NullPointerTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.base.Time.currentTimeZone;
import static io.spine.time.testing.TimeTests.avoidDayEdge;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("YearMonths should")
class YearMonthsTest extends AbstractDateTimeUtilityTest<YearMonth, java.time.YearMonth> {

    YearMonthsTest() {
        super(YearMonths.class,
              Now::asYearMonth,
              YearMonths::toString,
              YearMonths::parse,
              YearMonths.converter());
    }

    @Override
    void addDefaults(NullPointerTester nullTester) {
        // None.
    }

    private static void assertMonthsEqual(java.time.YearMonth jt, YearMonth value) {
        assertEquals(jt.getYear(), value.getYear());
        assertEquals(jt.getMonthValue(), value.getMonthValue());
    }

    @Nested
    @DisplayName("Create new instances")
    class Create {

        @Test
        @DisplayName("for current month")
        void currentMonth() {
            avoidDayEdge();
            assertMonthsEqual(java.time.YearMonth.now(currentTimeZone()), current());
        }

        @Test
        @DisplayName("by year and month")
        void yearMonth() {
            java.time.YearMonth ym = java.time.YearMonth.now(currentTimeZone());
            assertMonthsEqual(ym, YearMonths.of(ym.getYear(), ym.getMonthValue()));
        }
    }
}
