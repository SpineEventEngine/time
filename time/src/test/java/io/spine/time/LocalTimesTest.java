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
import io.spine.base.Time;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.test.DisplayNames.HAVE_PARAMETERLESS_CTOR;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.LocalTimes.of;
import static io.spine.time.LocalTimes.parse;
import static io.spine.time.LocalTimes.toJavaTime;
import static io.spine.time.SiTime.NANOS_PER_SECOND;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Alexander Aleksandrov
 * @author Alexander Yevsyukov
 */
@SuppressWarnings("ClassCanBeStatic")
@DisplayName("LocalTimes utility class should")
class LocalTimesTest {

    private java.time.LocalTime javaTimeNow;

    @BeforeEach
    void getCurrentTime() {
        javaTimeNow = java.time.LocalTime.now();
    }

    @Test
    @DisplayName(HAVE_PARAMETERLESS_CTOR)
    void haveUtilityConstructor() {
        assertHasPrivateParameterlessCtor(LocalTimes.class);
    }

    @Test
    @DisplayName("Convert to JavaTime value")
    void convertToJavaTime() {
        LocalTime now = LocalTimes.now();
        java.time.LocalTime javaTime = toJavaTime(now);

        assertEquals(now.getHour(), javaTime.getHour());
        assertEquals(now.getMinute(), javaTime.getMinute());
        assertEquals(now.getSecond(), javaTime.getSecond());
        assertEquals(now.getNano(), javaTime.getNano());
    }

    @Nested
    @DisplayName("Create new instance by")
    class Create {
        @Test
        @DisplayName("Java Time value")
        void byJavaTime() {
            java.time.LocalTime javaTime = java.time.LocalTime.now();
            LocalTime now = of(javaTime);

            assertEquals(javaTime.getHour(), now.getHour());
            assertEquals(javaTime.getMinute(), now.getMinute());
            assertEquals(javaTime.getSecond(), now.getSecond());
            assertEquals(javaTime.getNano(), now.getNano());
        }


        @Test
        @DisplayName("hours and minutes")
        void createByHoursAndMinutes() {
            LocalTime test = of(javaTimeNow.getHour(),
                                javaTimeNow.getMinute());

            assertEquals(javaTimeNow.getHour(), test.getHour());
            assertEquals(javaTimeNow.getMinute(), test.getMinute());
        }

        @Test
        @DisplayName("hours, minutes, and seconds")
        void createByHoursMinutesAndSeconds() {
            LocalTime test = of(javaTimeNow.getHour(),
                                javaTimeNow.getMinute(),
                                javaTimeNow.getSecond());

            assertEquals(javaTimeNow.getHour(), test.getHour());
            assertEquals(javaTimeNow.getMinute(), test.getMinute());
            assertEquals(javaTimeNow.getSecond(), test.getSecond());
        }

        @Test
        @DisplayName("hours, minutes, seconds, and nanos")
        void createWithNanoPrecision() {
            LocalTime test = of(javaTimeNow.getHour(),
                                javaTimeNow.getMinute(),
                                javaTimeNow.getSecond(),
                                javaTimeNow.getNano());

            assertTimeEqual(javaTimeNow, test);
        }
    }

    private static void assertTimeEqual(java.time.LocalTime jt, LocalTime lt) {
        assertEquals(jt.getHour(), lt.getHour());
        assertEquals(jt.getMinute(), lt.getMinute());
        assertEquals(jt.getSecond(), lt.getSecond());
        assertEquals(jt.getNano(), lt.getNano());
    }

    @Nested
    @DisplayName("Reject")
    class Arguments {

        @Test
        @DisplayName("null arguments")
        void nullCheck() {
            new NullPointerTester()
                    .setDefault(Timestamp.class, Time.getCurrentTime())
                    .setDefault(ZoneOffset.class, ZoneOffsets.utc())
                    .setDefault(LocalTime.class, LocalTimes.now())
                    .testAllPublicStaticMethods(LocalTimes.class);
        }

        @Nested
        @DisplayName("Hour value which is")
        class Hour {

            @Test
            void negative() {
                assertThrows(
                        IllegalArgumentException.class,
                        () -> of(-2, 20)
                );
            }

            @Test
            @DisplayName("out of 0..23 bounds")
            void outOfBounds() {
                assertThrows(
                        IllegalArgumentException.class,
                        () -> of(24, 0)
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
                        IllegalArgumentException.class,
                        () -> of(0, -20)
                );
            }

            @Test
            @DisplayName("out of 0..59 bounds")
            void outOfBounds() {
                assertThrows(
                        IllegalArgumentException.class,
                        () -> of(0, 60)
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
                        IllegalArgumentException.class,
                        () -> of(0, 0, -50)
                );
            }

            @Test
            @DisplayName("out of 0..59 bounds")
            void outOfBounds() {
                assertThrows(
                        IllegalArgumentException.class,
                        () -> of(0, 0, 60)
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
                        IllegalArgumentException.class,
                        () -> of(0, 0, 0, -1)
                );
            }

            @Test
            @DisplayName("out of bounds")
            void outOfBounds() {
                assertThrows(
                        IllegalArgumentException.class,
                        () -> of(0, 0, 0, NANOS_PER_SECOND)
                );
            }

        }
    }


    @Test
    @DisplayName("Convert to String and back")
    void stringAndBack() {
        LocalTime localTime = of(10, 20, 30, 50);

        String str = LocalTimes.toString(localTime);
        LocalTime convertedBack = parse(str);
        assertEquals(localTime, convertedBack);
    }
}
