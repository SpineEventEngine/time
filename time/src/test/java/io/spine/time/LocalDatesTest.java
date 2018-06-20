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

import static com.google.common.testing.SerializableTester.reserializeAndAssert;
import static io.spine.test.DisplayNames.HAVE_PARAMETERLESS_CTOR;
import static io.spine.test.DisplayNames.NOT_ACCEPT_NULLS;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.LocalDates.of;
import static io.spine.time.LocalDates.toJavaTime;
import static io.spine.time.testing.TimeTests.avoidDayEdge;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("ClassCanBeStatic")
@DisplayName("LocalDates should")
class LocalDatesTest {

    @Test
    @DisplayName(HAVE_PARAMETERLESS_CTOR)
    void haveUtilityConstructor() {
        assertHasPrivateParameterlessCtor(LocalDates.class);
    }

    @Test
    @DisplayName(NOT_ACCEPT_NULLS)
    void rejectNulls() {
        new NullPointerTester().testAllPublicStaticMethods(LocalDates.class);
    }

    private static void assertDatesEqual(java.time.LocalDate jt, LocalDate ld) {
        assertEquals(jt.getYear(), ld.getYear());
        assertEquals(jt.getMonthValue(), ld.getMonth()
                                           .getNumber());
        assertEquals(jt.getDayOfMonth(), ld.getDay());
    }

    @Test
    @DisplayName("Obtain current date")
    void obtainCurrentDate() {
        avoidDayEdge();
        LocalDate today = LocalDates.now();
        java.time.LocalDate jt = java.time.LocalDate.now();
        assertDatesEqual(jt, today);
    }

    @Nested
    @DisplayName("Create new instance by")
    class Create {

        @Test
        @DisplayName("year, month, and day")
        void byYearMonthDay() {
            int year = 2014;
            Month month = Month.JULY;
            int day = 20;

            LocalDate localDate = LocalDates.of(year, month, day);

            assertEquals(year, localDate.getYear());
            assertEquals(month, localDate.getMonth());
            assertEquals(day, localDate.getDay());
        }

        @Test
        @DisplayName("Java Time value")
        void byJavaTime() {
            java.time.LocalDate jt = java.time.LocalDate.now();
            LocalDate value = LocalDates.of(jt);
            assertDatesEqual(jt, value);
        }
    }

    @Nested
    @DisplayName("Reject")
    class Arguments {

        @Test
        @DisplayName("null arguments")
        void nullCheck() {
            new NullPointerTester()
                    .setDefault(LocalDate.class, LocalDates.now())
                    .setDefault(int.class, 1)
                    .testAllPublicStaticMethods(LocalDates.class);
        }

        @Test
        @DisplayName("negative year value")
        void negativeYear() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> LocalDates.of(-1987, Month.AUGUST, 20)
            );
        }

        @Test
        @DisplayName("negative day value")
        void negativeDay() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> LocalDates.of(1987, Month.AUGUST, -20)
            );
        }
    }

    @Test
    @DisplayName("convert to string and back")
    void stringify() {
        LocalDate today = LocalDates.now();
        String str = LocalDates.toString(today);
        assertEquals(today, LocalDates.parse(str));
    }

    @Test
    @DisplayName("convert to Java Time and back")
    void convert() {
        avoidDayEdge();
        LocalDate today = LocalDates.now();
        java.time.LocalDate converted = toJavaTime(today);
        assertEquals(today, of(converted));
    }

    @Test
    @DisplayName("have Serializable Converter")
    void serialize() {
        reserializeAndAssert(LocalDates.converter());
    }
}
