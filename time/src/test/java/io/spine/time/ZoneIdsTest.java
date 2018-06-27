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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.google.common.testing.SerializableTester.reserializeAndAssert;
import static io.spine.test.DisplayNames.HAVE_PARAMETERLESS_CTOR;
import static io.spine.test.DisplayNames.NOT_ACCEPT_NULLS;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static io.spine.time.ZoneIds.toJavaTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("ClassCanBeStatic")
@DisplayName("ZoneIds should")
class ZoneIdsTest extends AbstractDateTimeUtilityTest<ZoneId, java.time.ZoneId> {

    ZoneIdsTest() {
        super(ZoneIds.class, ZoneIds.converter());
    }

    @Override
    void addDefaults(NullPointerTester nullTester) {
        // None.
    }

    @Nested
    @DisplayName("Create new instance")
    class Create {

        @Test
        @DisplayName("for the current time-zone")
        void forCurrentTimeZone() {
            assertEquals(java.time.ZoneId.systemDefault()
                                         .getId(),
                         ZoneIds.systemDefault()
                                .getValue());
        }

        @Test
        @DisplayName("by an ID value")
        void byValue() {
            for (String id : java.time.ZoneId.getAvailableZoneIds()) {
                assertEquals(id, ZoneIds.of(id).getValue());
            }
        }

        @Test
        @DisplayName("by Java Time value")
        void byJavaTime() {
            ZoneId expected = ZoneIds.systemDefault();
            assertEquals(expected, ZoneIds.of(toJavaTime(expected)));
        }
    }

    @Nested
    @DisplayName("Convert from/to")
    class Convert {

        @Test
        @DisplayName("Java Time")
        void javaTime() {
            ZoneId expected = ZoneIds.systemDefault();
            Converter<java.time.ZoneId, ZoneId> converter = ZoneIds.converter();
            java.time.ZoneId converted = converter.reverse().convert(expected);

            assertEquals(expected, converter.convert(converted));
        }

        @Test
        @DisplayName("String")
        void toFromString() {
            ZoneId expected = ZoneIds.systemDefault();
            assertEquals(expected, ZoneIds.parse(ZoneIds.toString(expected)));
        }
    }
}
