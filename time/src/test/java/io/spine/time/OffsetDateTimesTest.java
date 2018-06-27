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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.time.Asserts.assertDatesEqual;
import static io.spine.time.Asserts.assertTimesEqual;
import static io.spine.time.OffsetDateTimes.of;
import static io.spine.time.OffsetDateTimes.toJavaTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("ClassCanBeStatic")
@DisplayName("OffsetDateTimes should")
public class OffsetDateTimesTest
        extends AbstractOffsetTimeTest<OffsetDateTime, java.time.OffsetDateTime> {

    private LocalDate date;
    private LocalTime time;

    OffsetDateTimesTest() {
        super(OffsetDateTimes.class, OffsetDateTimes.converter());
    }

    @Override
    protected void assertConversionAt(ZoneOffset zoneOffset) {
        OffsetDateTime now = OffsetDateTimes.now(zoneOffset);
        String str = OffsetDateTimes.toString(now);
        OffsetDateTime parsed = OffsetDateTimes.parse(str);

        assertEquals(now, parsed);
    }

    @Override
    void addDefaults(NullPointerTester nullTester) {
        nullTester.setDefault(LocalTime.class, LocalTimes.now())
                  .setDefault(ZoneOffset.class, ZoneOffsets.getDefault())
                  .setDefault(LocalDate.class, LocalDates.now());

    }

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        date = LocalDates.now();
        time = LocalTimes.now();
    }

    @Nested
    @DisplayName("Create new instance with")
    class Create {

        @Test
        @DisplayName("current date/time")
        void currentDateTime() {
            OffsetDateTime now = OffsetDateTimes.now();
            java.time.OffsetDateTime jn = toJavaTime(now);
            assertEqualDateTime(jn, now);
        }

        @Test
        @DisplayName("date/time at offset")
        void dateTimeAtOffset() {
            OffsetDateTime offsetDateTime = of(date, time, zoneOffset());

            LocalDate date = offsetDateTime.getDateTime()
                                           .getDate();
            LocalDates.checkDate(date);
            LocalTime time = offsetDateTime.getDateTime()
                                           .getTime();
            LocalTimes.checkTime(time);
            assertEquals(OffsetDateTimesTest.this.date, date);
            assertEquals(OffsetDateTimesTest.this.time, time);
            assertEquals(zoneOffset(), offsetDateTime.getOffset());
        }
    }

    private static void assertEqualDateTime(java.time.OffsetDateTime jt, OffsetDateTime ot) {
        LocalDateTime dateTime = ot.getDateTime();
        assertDatesEqual(jt.toLocalDate(), dateTime.getDate());
        assertTimesEqual(jt.toLocalTime(), dateTime.getTime());
    }
}
