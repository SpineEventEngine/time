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

import static io.spine.test.TestValues.random;
import static io.spine.time.ZoneOffsets.Parameter.HOURS;
import static io.spine.time.ZoneOffsets.Parameter.MINUTES;
import static java.lang.Math.abs;

/**
 * Abstract base for test of time with offset.
 *
 * @author Alexander Yevsyukov
 */
@SuppressWarnings("ClassCanBeStatic")
public abstract class AbstractZonedTimeTest {

    private ZoneOffset zoneOffset;

    protected abstract void assertConversionAt(ZoneOffset zoneOffset);

    private static ZoneOffset generateOffset() {
        // Reduce the hour range by one assuming minutes are also generated.
        int hours = random(HOURS.min() + 1, HOURS.max() - 1);
        int minutes = random(MINUTES.min(), MINUTES.max());
        // Make minutes of the same sign with hours.
        minutes = hours >= 0
                  ? abs(minutes)
                  : -abs(minutes);
        return ZoneOffsets.ofHoursMinutes(hours, minutes);
    }

    protected ZoneOffset zoneOffset() {
        return this.zoneOffset;
    }

    @BeforeEach
    public void setUp() {
        zoneOffset = generateOffset();
    }

    @SuppressWarnings("unused") // is used when running descending test suites
    @Nested
    @DisplayName("Convert values at")
    class Convert {

        @Test
        @DisplayName("UTC")
        void utc() {
            assertConversionAt(ZoneOffsets.utc());
        }

        @Test
        @DisplayName("current time zone")
        void currentTimeZone() {
            ZoneOffset zoneOffset = ZoneOffsets.getDefault();
            assertConversionAt(zoneOffset);
        }

        @Test
        @DisplayName("negative offset")
        void atNegativeOffset() {
            assertConversionAt(ZoneOffsets.ofHoursMinutes(-5, -30));
        }

        @Test
        @DisplayName("positive offset")
        void atPositiveOffset() {
            assertConversionAt(ZoneOffsets.ofHoursMinutes(7, 40));
        }
    }
}
