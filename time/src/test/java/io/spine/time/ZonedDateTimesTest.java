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
import com.google.common.testing.NullPointerTester;
import io.spine.validate.Validate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.google.common.testing.SerializableTester.reserializeAndAssert;
import static io.spine.test.DisplayNames.HAVE_PARAMETERLESS_CTOR;
import static io.spine.test.DisplayNames.NOT_ACCEPT_NULLS;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.Asserts.assertDatesEqual;
import static io.spine.time.ZonedDateTimes.toJavaTime;
import static io.spine.time.testing.TimeTests.avoidDayEdge;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ZonedDateTimes should")
class ZonedDateTimesTest {

    @Test
    @DisplayName(HAVE_PARAMETERLESS_CTOR)
    void utilityCtor() {
        assertHasPrivateParameterlessCtor(ZonedDateTimes.class);
    }

    @Test
    @DisplayName(NOT_ACCEPT_NULLS)
    void nullCheck() {
        new NullPointerTester()
                .setDefault(ZoneId.class, ZoneIds.systemDefault())
                .setDefault(LocalDateTime.class, LocalDateTimes.now())
                .testAllPublicStaticMethods(ZonedDateTimes.class);
    }

    @Nested
    @DisplayName("Create new instance")
    class Create {

        @Test
        @DisplayName("for the current date-time")
        void forCurrentDateTime() {
            avoidDayEdge();
            ZonedDateTime now = ZonedDateTimes.now();
            java.time.ZonedDateTime jt = java.time.ZonedDateTime.now();

            // Compare only dates as time would be different.
            LocalDateTime ldt = now.getDateTime();
            assertDatesEqual(jt.toLocalDate(), ldt.getDate());

            assertTrue(Validate.isNotDefault(ldt.getTime()));
        }

        @Test
        @DisplayName("by Java Time value")
        void byJavaTime() {
            ZonedDateTime expected = ZonedDateTimes.now();
            assertEquals(expected, ZonedDateTimes.of(toJavaTime(expected)));
        }
    }

    @Nested
    @DisplayName("Provide Converter from/to Java Time which")
    class Convert {

        private final Converter<java.time.ZonedDateTime, ZonedDateTime> converter =
                ZonedDateTimes.converter();

        @Test
        @DisplayName("converts values back and forth")
        void convert() {
            ZonedDateTime expected = ZonedDateTimes.now();
            java.time.ZonedDateTime converted = converter.reverse()
                                                         .convert(expected);
            assertEquals(expected, converter.convert(converted));
        }

        @Test
        @DisplayName("is serializable")
        void serialize() {
            reserializeAndAssert(converter);
        }
    }
}