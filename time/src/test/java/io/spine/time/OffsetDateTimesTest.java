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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.time.Asserts.assertDatesEqual;
import static io.spine.time.Asserts.assertTimesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("deprecation")
@DisplayName("`OffsetDateTimes` should")
public class OffsetDateTimesTest
        extends AbstractOffsetTimeTest<OffsetDateTime, java.time.OffsetDateTime> {

    private LocalDate date;
    private LocalTime time;

    OffsetDateTimesTest() {
        super(OffsetDateTimes.class,
              Now::asOffsetDateTime,
              OffsetDateTimes::toString,
              OffsetDateTimes::parse,
              OffsetDateTimes.converter());
    }

    @Override
    protected void assertConversionAt(ZoneOffset zoneOffset) {
        var now = Now.get(ZoneOffsets.toJavaTime(zoneOffset));
        var offsetDateTime = now.asOffsetDateTime();
        var str = OffsetDateTimes.toString(offsetDateTime);
        var parsed = OffsetDateTimes.parse(str);

        assertEquals(offsetDateTime, parsed);
    }

    @Override
    void addDefaults(NullPointerTester nullTester) {
        nullTester.setDefault(LocalTime.class, LocalTime.getDefaultInstance())
                  .setDefault(ZoneOffset.class, ZoneOffsets.getDefault())
                  .setDefault(LocalDate.class, LocalDate.getDefaultInstance());

    }

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        var now = Now.get();
        date = now.asLocalDate();
        time = now.asLocalTime();
    }

    @Nested
    @DisplayName("Create new instance with")
    class Create {

        @Test
        @DisplayName("current date/time")
        void currentDateTime() {
            var now = current();
            var jn = io.spine.time.OffsetDateTimes.toJavaTime(now);
            assertEqualDateTime(jn, now);
        }

        @Test
        @DisplayName("date/time at offset")
        void dateTimeAtOffset() {
            var offsetDateTime = OffsetDateTimes.of(date, time, zoneOffset());

            var date = offsetDateTime.getDateTime().getDate();
            LocalDates.checkDate(date);
            var time = offsetDateTime.getDateTime().getTime();
            assertEquals(OffsetDateTimesTest.this.date, date);
            assertEquals(OffsetDateTimesTest.this.time, time);
            assertEquals(zoneOffset(), offsetDateTime.getOffset());
        }
    }

    private static void assertEqualDateTime(java.time.OffsetDateTime jt, OffsetDateTime ot) {
        var dateTime = ot.getDateTime();
        assertDatesEqual(jt.toLocalDate(), dateTime.getDate());
        assertTimesEqual(jt.toLocalTime(), dateTime.getTime());
    }
}
