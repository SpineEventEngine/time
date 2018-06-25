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
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.Asserts.assertDatesEqual;
import static io.spine.time.LocalDateTimes.of;
import static io.spine.time.LocalDateTimes.toJavaTime;
import static io.spine.time.testing.TimeTests.avoidDayEdge;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Alexander Yevsyukov
 */
@SuppressWarnings("ClassCanBeStatic")
@DisplayName("LocalDateTimes should")
class LocalDateTimesTest {

    @Test
    @DisplayName(HAVE_PARAMETERLESS_CTOR)
    void utilityCtor() {
        assertHasPrivateParameterlessCtor(LocalDateTimes.class);
    }

    @Nested
    @DisplayName("Create new instance by")
    class Create {

        @Test
        @DisplayName("passed date and time")
        void dateTime() {
            LocalDate date = LocalDates.now();
            LocalTime time = LocalTimes.now();

            LocalDateTime dateTime = of(date, time);

            assertEquals(date, dateTime.getDate());
            assertEquals(time, dateTime.getTime());
        }

        @Test
        @DisplayName("current date-time")
        void currentDateTime() {
            avoidDayEdge();
            LocalDateTime now = LocalDateTimes.now();
            // Check that the date is the same. It's safe as we've not passed the end of the day.
            assertDatesEqual(java.time.LocalDate.now(), now.getDate());
            // We don't compare time here as it's surely changed.
        }
    }

    @Nested
    @DisplayName("Reject")
    class Arguments {

        @Test
        @DisplayName("null arguments")
        void rejectNulls() {
            new NullPointerTester()
                    .setDefault(LocalTime.class, LocalTimes.now())
                    .setDefault(LocalDate.class, LocalDates.now())
                    .testAllPublicStaticMethods(LocalDateTimes.class);
        }

        @Test
        @DisplayName("default date")
        void defaultDate() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> of(LocalDate.getDefaultInstance(), LocalTimes.now())
            );
        }

        @Test
        @DisplayName("default time")
        void defaultTime() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> of(LocalDates.now(), LocalTime.getDefaultInstance())
            );
        }
    }

    @Nested
    @DisplayName("Convert from/to")
    class Convert {

        //TODO:2018-06-25:alexander.yevsyukov: Test String conversion once implemented.

        @Test
        @DisplayName("Java Time")
        void javaTime() {
            LocalDateTime now = LocalDateTimes.now();
            java.time.LocalDateTime converted = toJavaTime(now);

            assertEquals(now, of(converted));
        }
    }

    @Test
    @DisplayName("have Serializable Converter")
    void serialize() {
        reserializeAndAssert(LocalDateTimes.converter());
    }
}
