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
import com.google.protobuf.Timestamp;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;

import static io.spine.base.Time.getCurrentTime;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.Calendars.at;
import static io.spine.time.Calendars.getDay;
import static io.spine.time.Calendars.getHours;
import static io.spine.time.Calendars.getMinutes;
import static io.spine.time.Calendars.getMonth;
import static io.spine.time.Calendars.getSeconds;
import static io.spine.time.Calendars.getYear;
import static io.spine.time.Calendars.getZoneOffset;
import static io.spine.time.OffsetDateTimes.addDays;
import static io.spine.time.OffsetDateTimes.addHours;
import static io.spine.time.OffsetDateTimes.addMinutes;
import static io.spine.time.OffsetDateTimes.addMonths;
import static io.spine.time.OffsetDateTimes.addNanos;
import static io.spine.time.OffsetDateTimes.addSeconds;
import static io.spine.time.OffsetDateTimes.addYears;
import static io.spine.time.OffsetDateTimes.of;
import static io.spine.time.OffsetDateTimes.subtractDays;
import static io.spine.time.OffsetDateTimes.subtractHours;
import static io.spine.time.OffsetDateTimes.subtractMinutes;
import static io.spine.time.OffsetDateTimes.subtractMonths;
import static io.spine.time.OffsetDateTimes.subtractNanos;
import static io.spine.time.OffsetDateTimes.subtractSeconds;
import static io.spine.time.OffsetDateTimes.subtractYears;
import static org.junit.Assert.assertEquals;

public class OffsetDateTimesShould extends AbstractZonedTimeTest {

    private static final int YEAR = 2012;
    private static final MonthOfYear MONTH = MonthOfYear.JULY;
    private static final int DAY = 16;
    private static final int HOURS = 9;
    private static final int MINUTES = 30;
    private static final int SECONDS = 23;
    private static final int NANOS = 122;

    private LocalDate gmtToday;
    private LocalTime now;
    private OffsetDateTime todayNow;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        gmtToday = LocalDates.of(YEAR, MONTH, DAY);
        now = LocalTimes.of(HOURS, MINUTES, SECONDS, NANOS);
        todayNow = OffsetDateTimes.now(zoneOffset);
    }

    @Test
    public void have_utility_constructor() {
        assertHasPrivateParameterlessCtor(OffsetDateTimes.class);
    }

    @Test
    public void get_current_date_time() {
        OffsetDateTime now = OffsetDateTimes.now(zoneOffset);
        Calendar cal = at(zoneOffset);

        LocalDate today = now.getDate();
        assertEquals(getYear(cal), today.getYear());
        assertEquals(getMonth(cal), today.getMonthValue());
        assertEquals(getDay(cal), today.getDay());

        LocalTime time = now.getTime();
        assertEquals(getHours(cal), time.getHour());
        assertEquals(getMinutes(cal), time.getMinute());
        assertEquals(getSeconds(cal), time.getSecond());
        assertEquals(getZoneOffset(cal), now.getOffset()
                                            .getAmountSeconds());
        /* We cannot check milliseconds and nanos due to time gap between object creation */
    }

    @Test
    public void create_instance_with_date_time_at_offset() {
        OffsetDateTime offsetDateTime = of(gmtToday, now, zoneOffset);

        LocalDate date = offsetDateTime.getDate();
        LocalDates.checkDate(date);
        LocalTime time = offsetDateTime.getTime();

        assertEquals(gmtToday, date);
        assertEquals(now, time);
        assertEquals(zoneOffset, offsetDateTime.getOffset());
    }

    /*
     * Math with date
     */

    @Test
    public void add_years() {
        int yearsToAdd = 3;
        OffsetDateTime offsetDateTime = of(gmtToday, now, zoneOffset);
        OffsetDateTime plusYears = addYears(offsetDateTime, yearsToAdd);

        LocalDate date = plusYears.getDate();
        LocalTime time = plusYears.getTime();
        ZoneOffset offset = plusYears.getOffset();

        assertEquals(YEAR + yearsToAdd, date.getYear());
        assertEquals(MONTH, date.getMonth());
        assertEquals(DAY, date.getDay());
        assertEquals(now, time);
        assertEquals(zoneOffset, offset);
    }

    @Test
    public void subtract_years() {
        int yearsToSubtract = 3;
        OffsetDateTime offsetDateTime = of(gmtToday, now, zoneOffset);
        OffsetDateTime minusYears = subtractYears(offsetDateTime, yearsToSubtract);

        LocalDate date = minusYears.getDate();
        LocalDates.checkDate(date);
        LocalTime time = minusYears.getTime();

        assertEquals(YEAR - yearsToSubtract, date.getYear());
        assertEquals(now, time);
        assertEquals(zoneOffset, offsetDateTime.getOffset());
    }

    @Test
    public void add_months() {
        int monthsToAdd = 3;
        OffsetDateTime offsetDateTime = of(gmtToday, now, zoneOffset);
        OffsetDateTime plusYears = addMonths(offsetDateTime, monthsToAdd);

        LocalDate date = plusYears.getDate();
        LocalTime time = plusYears.getTime();
        ZoneOffset offset = plusYears.getOffset();

        assertEquals(YEAR, date.getYear());
        assertEquals(DAY, date.getDay());
        assertEquals(MONTH.getNumber() + monthsToAdd, date.getMonth()
                                                          .getNumber());
        assertEquals(now, time);
        assertEquals(zoneOffset, offset);
    }

    @Test
    public void subtract_months() {
        int monthsToSubtract = 3;
        OffsetDateTime offsetDateTime = of(gmtToday, now, zoneOffset);
        OffsetDateTime minusYears = subtractMonths(offsetDateTime, monthsToSubtract);

        LocalDate date = minusYears.getDate();
        LocalTime time = minusYears.getTime();
        LocalDates.checkDate(date);

        assertEquals(YEAR, date.getYear());
        assertEquals(MONTH.getNumber() - monthsToSubtract, date.getMonth()
                                                               .getNumber());
        assertEquals(DAY, date.getDay());
        assertEquals(now, time);
        assertEquals(zoneOffset, offsetDateTime.getOffset());
    }

    @Test
    public void add_days() {
        int daysToAdd = 3;
        OffsetDateTime offsetDateTime = of(gmtToday, now, zoneOffset);
        OffsetDateTime plusYears = addDays(offsetDateTime, daysToAdd);

        LocalDate date = plusYears.getDate();
        LocalTime time = plusYears.getTime();
        ZoneOffset offset = plusYears.getOffset();

        assertEquals(YEAR, date.getYear());
        assertEquals(MONTH, date.getMonth());
        assertEquals(DAY + daysToAdd, date.getDay());
        assertEquals(now, time);
        assertEquals(zoneOffset, offset);
    }

    @Test
    public void subtract_days() {
        int daysToSubtract = 3;
        OffsetDateTime offsetDateTime = of(gmtToday, now, zoneOffset);
        OffsetDateTime minusYears = subtractDays(offsetDateTime, daysToSubtract);

        LocalDate date = minusYears.getDate();
        LocalTime time = minusYears.getTime();
        ZoneOffset offset = minusYears.getOffset();

        assertEquals(YEAR, date.getYear());
        assertEquals(MONTH, date.getMonth());
        assertEquals(DAY - daysToSubtract, date.getDay());
        assertEquals(now, time);
        assertEquals(zoneOffset, offset);
    }

    /*
     * Math with time
     */

    @Test
    public void add_Hour() {
        OffsetDateTime dateTime = of(gmtToday, now, zoneOffset);
        int hoursToAdd = 2;
        OffsetDateTime withAddedHours = addHours(dateTime, hoursToAdd);

        LocalDate date = withAddedHours.getDate();
        LocalTime time = withAddedHours.getTime();

        assertEquals(gmtToday, date);
        assertEquals(HOURS + hoursToAdd, time.getHour());
        assertEquals(MINUTES, time.getMinute());
        assertEquals(SECONDS, time.getSecond());
        assertEquals(NANOS, time.getNano());
        assertEquals(zoneOffset, withAddedHours.getOffset());
    }

    @Test
    public void subtract_Hour() {
        int hoursToSubtract = 4;
        OffsetDateTime offsetDateTime = of(gmtToday, now, zoneOffset);
        OffsetDateTime minusHours = subtractHours(offsetDateTime, hoursToSubtract);

        LocalDate date = minusHours.getDate();
        LocalTime time = minusHours.getTime();
        ZoneOffset offset = minusHours.getOffset();

        assertEquals(gmtToday, date);
        assertEquals(HOURS - hoursToSubtract, time.getHour());
        assertEquals(MINUTES, time.getMinute());
        assertEquals(SECONDS, time.getSecond());
        assertEquals(NANOS, time.getNano());
        assertEquals(zoneOffset, offset);
    }

    @Test
    public void add_minutes() {
        int minutesToAdd = 11;
        OffsetDateTime dateTime = of(gmtToday, now, zoneOffset);
        OffsetDateTime withAddedMinutes = addMinutes(dateTime, minutesToAdd);

        LocalDate date = withAddedMinutes.getDate();
        LocalTime time = withAddedMinutes.getTime();
        ZoneOffset offset = withAddedMinutes.getOffset();

        assertEquals(gmtToday, date);
        assertEquals(HOURS, time.getHour());
        assertEquals(MINUTES + minutesToAdd, time.getMinute());
        assertEquals(SECONDS, time.getSecond());
        assertEquals(NANOS, time.getNano());
        assertEquals(zoneOffset.getAmountSeconds(), offset.getAmountSeconds());
    }

    @Test
    public void subtract_minutes() {
        int minutesToSubtract = 11;
        OffsetDateTime offsetDateTime = of(gmtToday, now, zoneOffset);
        OffsetDateTime minusMinutes = subtractMinutes(offsetDateTime, minutesToSubtract);

        LocalDate date = minusMinutes.getDate();
        LocalTime time = minusMinutes.getTime();
        ZoneOffset offset = minusMinutes.getOffset();

        assertEquals(gmtToday, date);
        assertEquals(HOURS, time.getHour());
        assertEquals(MINUTES - minutesToSubtract, time.getMinute());
        assertEquals(SECONDS, time.getSecond());
        assertEquals(NANOS, time.getNano());
        assertEquals(zoneOffset, offset);
    }

    @Test
    public void add_seconds() {
        int secondsToAdd = 18;
        OffsetDateTime dateTime = of(gmtToday, now, zoneOffset);
        OffsetDateTime withAddedSeconds = addSeconds(dateTime, secondsToAdd);

        LocalDate date = withAddedSeconds.getDate();
        LocalTime time = withAddedSeconds.getTime();
        ZoneOffset offset = withAddedSeconds.getOffset();

        assertEquals(gmtToday, date);
        assertEquals(HOURS, time.getHour());
        assertEquals(MINUTES, time.getMinute());
        assertEquals(SECONDS + secondsToAdd, time.getSecond());
        assertEquals(NANOS, time.getNano());
        assertEquals(zoneOffset, offset);
    }

    @Test
    public void subtract_seconds() {
        int secondsToSubtract = 18;
        OffsetDateTime dateTime = of(gmtToday, now, zoneOffset);
        OffsetDateTime withSubtractedSeconds = subtractSeconds(dateTime, secondsToSubtract);

        LocalDate date = withSubtractedSeconds.getDate();
        LocalTime time = withSubtractedSeconds.getTime();
        ZoneOffset offset = withSubtractedSeconds.getOffset();

        assertEquals(gmtToday, date);
        assertEquals(HOURS, time.getHour());
        assertEquals(MINUTES, time.getMinute());
        assertEquals(SECONDS - secondsToSubtract, time.getSecond());
        assertEquals(NANOS, time.getNano());
        assertEquals(zoneOffset, offset);
    }

    @Test
    public void add_millis() {
        int millisToAdd = 118;
        OffsetDateTime dateTime = of(gmtToday, now, zoneOffset);
        OffsetDateTime withAddedMillis = addNanos(dateTime, millisToAdd);

        LocalDate date = withAddedMillis.getDate();
        LocalTime time = withAddedMillis.getTime();
        ZoneOffset offset = withAddedMillis.getOffset();

        assertEquals(gmtToday, date);
        assertEquals(HOURS, time.getHour());
        assertEquals(MINUTES, time.getMinute());
        assertEquals(SECONDS, time.getSecond());
        assertEquals(NANOS, time.getNano());
        assertEquals(zoneOffset, offset);
    }

    @Test
    public void subtract_millis() {
        int millisToSubtract = 118;
        OffsetDateTime dateTime = of(gmtToday, now, zoneOffset);
        OffsetDateTime withSubtractedMillis = subtractNanos(dateTime, millisToSubtract);

        LocalDate date = withSubtractedMillis.getDate();
        LocalTime time = withSubtractedMillis.getTime();
        ZoneOffset offset = withSubtractedMillis.getOffset();

        assertEquals(gmtToday, date);
        assertEquals(HOURS, time.getHour());
        assertEquals(MINUTES, time.getMinute());
        assertEquals(SECONDS, time.getSecond());
        assertEquals(NANOS, time.getNano());
        assertEquals(zoneOffset, offset);
    }

    @Test
    public void pass_null_tolerance_test() {
        new NullPointerTester()
                .setDefault(Timestamp.class, getCurrentTime())
                .setDefault(OffsetDateTime.class, OffsetDateTimes.now(zoneOffset))
                .setDefault(ZoneOffset.class, zoneOffset)
                .setDefault(LocalTime.class, LocalTimes.now())
                .setDefault(LocalDate.class, LocalDates.now())
                .testAllPublicStaticMethods(OffsetDateTimes.class);
    }

    /*
     * Illegal argsIllegal args. check for math with years.
     */

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_years_to_add() {
        addYears(todayNow, -5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_years_to_add() {
        addYears(todayNow, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_years_to_subtract() {
        subtractYears(todayNow, -6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_years_to_subtract() {
        subtractYears(todayNow, 0);
    }

    /*
     * Illegal args. check for math with months.
     */

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_months_to_add() {
        addMonths(todayNow, -5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_months_to_add() {
        addMonths(todayNow, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_months_to_subtract() {
        OffsetDateTimes.subtractMonths(todayNow, -6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_months_to_subtract() {
        OffsetDateTimes.subtractMonths(todayNow, 0);
    }

    /*
     * Illegal args. check for math with days.
     */

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_days_to_add() {
        addDays(todayNow, -5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_days_to_add() {
        addDays(todayNow, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_days_to_subtract() {
        subtractDays(todayNow, -6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_days_to_subtract() {
        subtractDays(todayNow, 0);
    }

    /*
     * Illegal args. check for math with hours.
     */

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_hours_to_add() {
        addHours(todayNow, -5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_hours_to_add() {
        addHours(todayNow, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_hours_to_subtract() {
        subtractHours(todayNow, -6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_hours_to_subtract() {
        subtractHours(todayNow, 0);
    }

    /*
     * Illegal args. check for math with minutes.
     */

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_minutes_to_add() {
        addMinutes(todayNow, -7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_minutes_to_add() {
        addMinutes(todayNow, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_minutes_to_subtract() {
        subtractMinutes(todayNow, -8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_minutes_to_subtract() {
        subtractMinutes(todayNow, 0);
    }

    /*
     * Illegal args. check for math with seconds.
     */

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_seconds_to_add() {
        addSeconds(todayNow, -25);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_seconds_to_add() {
        addSeconds(todayNow, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_seconds_to_subtract() {
        subtractSeconds(todayNow, -27);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_seconds_to_subtract() {
        subtractSeconds(todayNow, 0);
    }

    /*
     * Illegal args. check for math with millis.
     */

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_millis_to_add() {
        addNanos(todayNow, -500);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_millis_to_add() {
        addNanos(todayNow, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_negative_millis_to_subtract() {
        subtractNanos(todayNow, -270);
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_accept_zero_millis_to_subtract() {
        subtractNanos(todayNow, 0);
    }

    /*
     * Stringification
     */

    @Override
    protected void assertConversionAt(ZoneOffset zoneOffset) throws ParseException {
        OffsetDateTime now = OffsetDateTimes.now(zoneOffset);
        String str = OffsetDateTimes.toString(now);
        OffsetDateTime parsed = OffsetDateTimes.parse(str);

        assertEquals(now, parsed);
    }
}
