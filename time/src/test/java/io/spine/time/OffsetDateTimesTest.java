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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.test.DisplayNames.HAVE_PARAMETERLESS_CTOR;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.OffsetDateTimes.of;
import static io.spine.time.OffsetDateTimes.toJavaTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("ClassCanBeStatic")
@DisplayName("OffsetDateTimes should")
public class OffsetDateTimesTest extends AbstractZonedTimeTest {

    private static final int YEAR = 2012;
    private static final Month MONTH = Month.JULY;
    private static final int DAY = 16;
    private static final int HOURS = 9;
    private static final int MINUTES = 30;
    private static final int SECONDS = 23;
    private static final int NANOS = 122;

    private LocalDate gmtToday;
    private LocalTime now;

    @Override
    protected void assertConversionAt(ZoneOffset zoneOffset) {
        OffsetDateTime now = OffsetDateTimes.now();
        String str = OffsetDateTimes.toString(now);
        OffsetDateTime parsed = OffsetDateTimes.parse(str);

        assertEquals(now, parsed);
    }

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        gmtToday = LocalDates.of(YEAR, MONTH, DAY);
        now = LocalTimes.of(HOURS, MINUTES, SECONDS, NANOS);
    }

    @Test
    @DisplayName(HAVE_PARAMETERLESS_CTOR)
    void utilityConstructor() {
        assertHasPrivateParameterlessCtor(OffsetDateTimes.class);
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
            OffsetDateTime offsetDateTime = of(gmtToday, now, zoneOffset());

            LocalDate date = offsetDateTime.getDate();
            LocalDates.checkDate(date);
            LocalTime time = offsetDateTime.getTime();

            assertEquals(gmtToday, date);
            assertEquals(now, time);
            assertEquals(zoneOffset(), offsetDateTime.getOffset());
        }
    }

    private static void assertEqualDateTime(java.time.OffsetDateTime jt, OffsetDateTime ot) {
        LocalDate date = ot.getDate();
        assertEquals(jt.getYear(), date.getYear());
        assertEquals(jt.getMonthValue(), date.getMonthValue());
        assertEquals(jt.getDayOfMonth(), date.getDay());

        LocalTime time = ot.getTime();
        assertEquals(jt.getHour(), time.getHour());
        assertEquals(jt.getMinute(), time.getMinute());
        assertEquals(jt.getSecond(), time.getSecond());
        assertEquals(jt.getNano(), time.getNano());
    }
}
