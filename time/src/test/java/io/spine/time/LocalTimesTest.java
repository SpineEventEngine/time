/*
 * Copyright 2019, TeamDev. All rights reserved.
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
import io.spine.validate.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static io.spine.base.Time.currentTimeZone;
import static io.spine.time.Asserts.assertTimesEqual;
import static io.spine.time.LocalTimes.toJavaTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("LocalTimes should")
class LocalTimesTest extends AbstractDateTimeUtilityTest<LocalTime, java.time.LocalTime> {

    /** The count of nanoseconds in one second. */
    @SuppressWarnings("NumericCastThatLosesPrecision") // Known to fit.
    private static final int NANOS_PER_SECOND = (int) TimeUnit.SECONDS.toNanos(1);

    private java.time.LocalTime javaTimeNow;

    LocalTimesTest() {
        super(LocalTimes.class,
              Now::asLocalTime,
              LocalTimes::toString,
              LocalTimes::parse,
              LocalTimes.converter());
    }

    @Override
    void addDefaults(NullPointerTester nullTester) {
        // None.
    }

    @BeforeEach
    void initLocalTimeNow() {
        javaTimeNow = java.time.LocalTime.now(currentTimeZone());
    }

    @Test
    @DisplayName("convert to JavaTime value")
    void convertToJavaTime() {
        LocalTime now = LocalTimes.of(13, 15);
        java.time.LocalTime javaTime = toJavaTime(now);

        assertTimesEqual(javaTime, now);
    }

    @Nested
    @DisplayName("Create new instance by")
    class Create {

        @Test
        @DisplayName("Java Time value")
        void byJavaTime() {
            java.time.LocalTime javaTime = java.time.LocalTime.now(currentTimeZone());
            LocalTime now = LocalTimes.of(javaTime);

            assertTimesEqual(javaTime, now);
        }

        @Test
        @DisplayName("hours and minutes")
        void createByHoursAndMinutes() {
            LocalTime test = LocalTimes.of(javaTimeNow.getHour(),
                                           javaTimeNow.getMinute());

            assertEquals(javaTimeNow.getHour(), test.getHour());
            assertEquals(javaTimeNow.getMinute(), test.getMinute());
        }

        @Test
        @DisplayName("hours, minutes, and seconds")
        void createByHoursMinutesAndSeconds() {
            LocalTime test = LocalTimes.of(javaTimeNow.getHour(),
                                           javaTimeNow.getMinute(),
                                           javaTimeNow.getSecond());
            assertEquals(javaTimeNow.getHour(), test.getHour());
            assertEquals(javaTimeNow.getMinute(), test.getMinute());
            assertEquals(javaTimeNow.getSecond(), test.getSecond());
        }

        @Test
        @DisplayName("hours, minutes, seconds, and nanos")
        void createWithNanoPrecision() {
            LocalTime test = LocalTimes.of(javaTimeNow.getHour(),
                                           javaTimeNow.getMinute(),
                                           javaTimeNow.getSecond(),
                                           javaTimeNow.getNano());

            assertTimesEqual(javaTimeNow, test);
        }

    }

    @Nested
    @DisplayName("Reject")
    class Arguments {

        @Nested
        @DisplayName("Hour value which is")
        class Hour {

            @Test
            void negative() {
                assertThrows(
                        ValidationException.class,
                        () -> LocalTimes.of(-2, 20)
                );
            }

            @Test
            @DisplayName("out of 0..23 bounds")
            void outOfBounds() {
                assertThrows(
                        ValidationException.class,
                        () -> LocalTimes.of(24, 0)
                );
            }
        }

        @Nested
        @DisplayName("Minutes value which is")
        class Minutes {

            @Test
            @DisplayName("negative")
            void negative() {
                assertThrows(
                        ValidationException.class,
                        () -> LocalTimes.of(0, -20)
                );
            }

            @Test
            @DisplayName("out of 0..59 bounds")
            void outOfBounds() {
                assertThrows(
                        ValidationException.class,
                        () -> LocalTimes.of(0, 60)
                );
            }
        }

        @Nested
        @DisplayName("Seconds value which is")
        class Seconds {

            @Test
            @DisplayName("negative")
            void negative() {
                assertThrows(
                        ValidationException.class,
                        () -> LocalTimes.of(0, 0, -50)
                );
            }

            @Test
            @DisplayName("out of 0..59 bounds")
            void outOfBounds() {
                assertThrows(
                        ValidationException.class,
                        () -> LocalTimes.of(0, 0, 60)
                );
            }
        }

        @Nested
        @DisplayName("Nano value which is")
        class Nanos {

            @Test
            @DisplayName("negative")
            void negative() {
                assertThrows(
                        ValidationException.class,
                        () -> LocalTimes.of(0, 0, 0, -1)
                );
            }

            @Test
            @DisplayName("out of bounds")
            void outOfBounds() {
                assertThrows(
                        ValidationException.class,
                        () -> LocalTimes.of(0, 0, 0, NANOS_PER_SECOND)
                );
            }
        }
    }

    @Test
    @DisplayName("parse midnight string")
    void midnightString() {
        assertEquals(LocalTime.getDefaultInstance(), LocalTimes.parse("00:00"));
        assertEquals(LocalTime.getDefaultInstance(), LocalTimes.parse("00:00:00"));
    }

    @Test
    @DisplayName("builds a midnight date")
    void buildMidnight() {
        LocalTime time = LocalTime
                .vBuilder()
                .setHour(0)
                .setMinute(0)
                .setSecond(0)
                .build();
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
        assertEquals(0, time.getSecond());
    }
}
