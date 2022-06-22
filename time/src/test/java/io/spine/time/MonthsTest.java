/*
 * Copyright 2022, TeamDev. All rights reserved.
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

import java.time.LocalDate;

import static io.spine.base.Time.currentTimeZone;
import static io.spine.time.Months.checkMonth;
import static io.spine.time.Months.toJavaTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Months should")
class MonthsTest extends AbstractDateTimeUtilityTest<Month, java.time.Month> {

    MonthsTest() {
        super(Months.class,
              Now::asMonth,
              Months::toString,
              Months::parse,
              Months.converter());
    }

    @Override
    void addDefaults(NullPointerTester nullTester) {
        // None.
    }

    @Nested
    @DisplayName("Create an instance")
    class Create {

        @Test
        @DisplayName("by java.time.LocalDate")
        void fromJavaTimeLocalDate() {
            LocalDate today = LocalDate.now(currentTimeZone());
            Month month = Months.of(today);

            assertEquals(today.getMonthValue(), month.getNumber());
        }

        @Test
        @DisplayName("by month number")
        void fromNumber() {
            for (int month = java.time.Month.JANUARY.getValue();
                 month <= java.time.Month.DECEMBER.getValue();
                 month++) {
                 assertEquals(month, Months.of(month).getNumber());
            }
        }
    }

    @Nested
    @DisplayName("Reject")
    class Arguments {

        @Test
        @DisplayName("invalid month value")
        void outOfBoundsMonth() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> checkMonth(0)
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> checkMonth(13)
            );
        }

        @Test
        @DisplayName("invalid Month instance")
        void invalidMonth() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> toJavaTime(Month.MONTH_UNDEFINED)
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> toJavaTime(Month.UNRECOGNIZED)
            );
        }
    }
}
