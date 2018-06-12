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
import com.google.protobuf.Duration;
import io.spine.string.Stringifier;
import io.spine.time.string.TimeStringifiers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.test.DisplayNames.HAVE_PARAMETERLESS_CTOR;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.Durations2.ZERO;
import static io.spine.time.Durations2.add;
import static io.spine.time.Durations2.fromHours;
import static io.spine.time.Durations2.fromMinutes;
import static io.spine.time.Durations2.getHours;
import static io.spine.time.Durations2.getMinutes;
import static io.spine.time.Durations2.hours;
import static io.spine.time.Durations2.hoursAndMinutes;
import static io.spine.time.Durations2.isGreaterThan;
import static io.spine.time.Durations2.isLessThan;
import static io.spine.time.Durations2.isNegative;
import static io.spine.time.Durations2.isPositive;
import static io.spine.time.Durations2.isPositiveOrZero;
import static io.spine.time.Durations2.isZero;
import static io.spine.time.Durations2.minutes;
import static io.spine.time.Durations2.nanos;
import static io.spine.time.Durations2.seconds;
import static io.spine.time.Durations2.toMinutes;
import static io.spine.time.Durations2.toNanos;
import static io.spine.time.Durations2.toSeconds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Alexander Yevsyukov
 */
@SuppressWarnings({"MagicNumber", "ClassWithTooManyMethods"})
@DisplayName("Durations2 should")
class Durations2Test {

    @Test
    @DisplayName(HAVE_PARAMETERLESS_CTOR)
    void utilityConstructor() {
        assertHasPrivateParameterlessCtor(Durations2.class);
    }

    @Test
    @DisplayName("have ZERO constant")
    void zeroConstant() {
        assertEquals(0, toNanos(ZERO));
    }

    @Nested
    @DisplayName("Convert a number of hours")
    class HourConversion {

        private void test(long hours) {
            Duration expected = seconds(hoursToSeconds(hours));
            Duration actual = fromHours(hours);
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("zero value")
        void zero() {
            test(0);
        }

        @Test
        @DisplayName("positive value")
        void positive() {
            test(36);
        }

        @Test
        @DisplayName("negative value")
        void negative() {
            test(-384);
        }
    }

    @Nested
    @DisplayName("Fail if")
    class MathError {

        @Test
        @DisplayName("hours value is too big")
        void onHugeHours() {
            assertThrows(
                    ArithmeticException.class,
                    () -> hours(Long.MAX_VALUE)
            );
        }

        @Test
        @DisplayName("minutes value is too big")
        void onHugeMinutes() {
            assertThrows(
                    ArithmeticException.class,
                    () -> minutes(Long.MAX_VALUE)
            );
        }
    }

    @Nested
    @DisplayName("Add")
    class Add {

        @Test
        @DisplayName("two nulls -> ZERO")
        void nullPlusNull() {
            assertEquals(ZERO, add(null, null));
        }

        @Test
        @DisplayName("null returning same instance")
        void sameWithNull() {
            Duration duration = seconds(525);
            assertSame(duration, add(duration, null));
            assertSame(duration, add(null, duration));
        }
    }

    @Test
    public void add_positive_durations() {
        addDurationsTest(25, 5);
        addDurationsTest(300, 338);
    }

    @Test
    public void add_negative_durations() {
        addDurationsTest(-25, -5);
        addDurationsTest(-300, -338);
    }

    @Test
    public void add_negative_and_positive_durations() {
        addDurationsTest(25, -5);
        addDurationsTest(-300, 338);
    }


    private static void addDurationsTest(long seconds1, long seconds2) {

        final long secondsTotal = seconds1 + seconds2;
        final Duration sumExpected = seconds(secondsTotal);

        final Duration sumActual = add(seconds(seconds1), seconds(seconds2));

        assertEquals(sumExpected, sumActual);
    }
    

    @Test
    public void convert_hours_and_minutes_to_duration() {

        final long hours = 3;
        final long minutes = 25;
        final long secondsTotal = hoursToSeconds(hours) + minutesToSeconds(minutes);
        final Duration expected = seconds(secondsTotal);

        final Duration actual = hoursAndMinutes(hours, minutes);

        assertEquals(expected, actual);
    }

    @Test
    public void convert_duration_to_nanoseconds() {
        assertEquals(10, toNanos(nanos(10)));
        assertEquals(-256, toNanos(nanos(-256)));
    }

    @Test
    public void convert_duration_to_seconds() {
        assertEquals(1, toSeconds(seconds(1)));
        assertEquals(-256, toSeconds(seconds(-256)));
    }

    @Test
    public void convert_duration_to_minutes() {
        assertEquals(1, toMinutes(minutes(1)));
        assertEquals(-256, toMinutes(minutes(-256)));
    }

    @Test
    public void return_hours_from_duration() {
        assertEquals(1, getHours(fromHours(1)));
        assertEquals(-256, getHours(fromHours(-256)));
    }

    @Test
    public void return_remainder_of_minutes_from_duration() {
        final long minutesRemainder = 8;
        final long minutesTotal = minutesRemainder + 60; // add 1 hour
        assertEquals(minutesRemainder, getMinutes(fromMinutes(minutesTotal)));
    }

    @Test
    public void verify_if_positive_or_zero() {
        assertTrue(isPositiveOrZero(seconds(360)));
        assertTrue(isPositiveOrZero(seconds(0)));
        assertFalse(isPositiveOrZero(seconds(-32)));
    }

    @Test
    public void verify_if_positive() {
        assertTrue(isPositive(seconds(360)));
        assertFalse(isPositive(seconds(0)));
        assertFalse(isPositive(seconds(-32)));
    }

    @Test
    public void verify_if_zero() {
        assertTrue(isZero(seconds(0)));
        assertFalse(isZero(seconds(360)));
        assertFalse(isZero(seconds(-32)));
    }

    @Test
    public void verify_if_negative() {
        assertTrue(isNegative(seconds(-32)));
        assertFalse(isNegative(seconds(360)));
        assertFalse(isNegative(seconds(0)));
    }

    @Test
    public void verify_if_greater() {
        assertTrue(isGreaterThan(seconds(64), seconds(2)));
        assertFalse(isGreaterThan(seconds(2), seconds(64)));
        assertFalse(isGreaterThan(seconds(5), seconds(5)));
    }

    @Test
    public void verify_if_less() {
        assertTrue(isLessThan(seconds(2), seconds(64)));
        assertFalse(isLessThan(seconds(64), seconds(2)));
        assertFalse(isLessThan(seconds(5), seconds(5)));
    }
    
    @Test
    public void pass_the_null_tolerance_check() {
        new NullPointerTester()
                .setDefault(Duration.class, Duration.getDefaultInstance())
                .testStaticMethods(Durations2.class, NullPointerTester.Visibility.PACKAGE);
    }

    private static long hoursToSeconds(long hours) {
        return hours * 60L * 60L;
    }

    private static long minutesToSeconds(long minutes) {
        return minutes * 60L;
    }

    @Test
    public void provide_stringifier() {
        final Stringifier<Duration> stringifier = TimeStringifiers.forDuration();
        final Duration duration = hoursAndMinutes(10, 20);
        assertEquals(duration, stringifier.reverse()
                                          .convert(stringifier.convert(duration)));
    }
}
