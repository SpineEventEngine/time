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
import static io.spine.protobuf.Messages.isNotDefault;
import static io.spine.time.Asserts.assertDatesEqual;
import static io.spine.time.ZonedDateTimes.toJavaTime;
import static io.spine.time.testing.TimeTests.avoidDayEdge;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("`ZonedDateTimes` should")
class ZonedDateTimesTest
        extends AbstractDateTimeUtilityTest<ZonedDateTime, java.time.ZonedDateTime> {

    ZonedDateTimesTest() {
        super(ZonedDateTimes.class,
              Now::asZonedDateTime,
              ZonedDateTimes::toString,
              ZonedDateTimes::parse,
              ZonedDateTimes.converter());
    }

    @Override
    void addDefaults(NullPointerTester nullTester) {
        nullTester.setDefault(ZoneId.class, ZoneIds.systemDefault())
                  .setDefault(LocalDateTime.class, Now.get().asLocalDateTime());

    }

    @Nested
    @DisplayName("create new instance")
    class Create {

        @Test
        @DisplayName("for the current date-time")
        void forCurrentDateTime() {
            avoidDayEdge();
            var now = current();
            var jt = java.time.ZonedDateTime.now(currentTimeZone());

            // Compare only dates as time would be different.
            var ldt = now.getDateTime();
            assertDatesEqual(jt.toLocalDate(), ldt.getDate());

            assertTrue(isNotDefault(ldt.getTime()));
        }

        @Test
        @DisplayName("by Java Time value")
        void byJavaTime() {
            var expected = current();
            assertEquals(expected, ZonedDateTimes.of(toJavaTime(expected)));
        }

        @Test
        @DisplayName("for local date/time and time-zone")
        void byDateTimeAndOffset() {
            var expectedTime = Now.get().asLocalDateTime();
            var expectedZone = ZoneIds.systemDefault();

            var value = ZonedDateTimes.of(expectedTime, expectedZone);
            assertEquals(expectedTime, value.getDateTime());
            assertEquals(expectedZone, value.getZone());
        }
    }

    @Nested
    @DisplayName("reject")
    class Arguments {

        @Test
        @DisplayName("default values")
        void defaultValues() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> ZonedDateTimes.of(LocalDateTime.getDefaultInstance(),
                                            ZoneIds.systemDefault())
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> ZonedDateTimes.of(Now.get().asLocalDateTime(),
                                            ZoneId.getDefaultInstance())
            );
        }
    }
}
