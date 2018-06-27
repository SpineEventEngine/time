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

import com.google.common.testing.NullPointerTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.time.YearMonths.toJavaTime;
import static io.spine.time.testing.TimeTests.avoidDayEdge;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alexander Yevsyukov
 */
@SuppressWarnings("ClassCanBeStatic")
@DisplayName("YearMonths should")
class YearMonthsTest extends AbstractDateTimeUtilityTest<YearMonth, java.time.YearMonth> {

    YearMonthsTest() {
        super(YearMonths.class, YearMonths::now, YearMonths.converter());
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
            assertMonthsEqual(java.time.YearMonth.now(), YearMonths.now());
        }

        @Test
        @DisplayName("by year and month")
        void yearMonth() {
            java.time.YearMonth ym = java.time.YearMonth.now();
            assertMonthsEqual(ym, YearMonths.of(ym.getYear(), ym.getMonthValue()));
        }
    }

    @Nested
    @DisplayName("Convert from/to")
    class Convert {

        @Test
        @DisplayName("Java Time")
        void fromJavaTime() {
            java.time.YearMonth expected = java.time.YearMonth.now();
            YearMonth converted = YearMonths.of(expected);
            assertEquals(expected, toJavaTime(converted));
        }

        @Test
        @DisplayName("String")
        void fromString() {
            YearMonth expected = YearMonths.now();
            assertEquals(expected, YearMonths.parse(YearMonths.toString(expected)));
        }
    }
}
