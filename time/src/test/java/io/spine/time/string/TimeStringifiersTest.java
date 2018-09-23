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

package io.spine.time.string;

import com.google.protobuf.Duration;
import com.google.protobuf.Timestamp;
import io.spine.string.Stringifier;
import io.spine.string.StringifierRegistry;
import io.spine.testing.UtilityClassTest;
import io.spine.time.LocalDate;
import io.spine.time.LocalTime;
import io.spine.time.OffsetDateTime;
import io.spine.time.OffsetTime;
import io.spine.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.time.string.TimeStringifiers.forDuration;
import static io.spine.time.string.TimeStringifiers.forLocalDate;
import static io.spine.time.string.TimeStringifiers.forLocalTime;
import static io.spine.time.string.TimeStringifiers.forOffsetDateTime;
import static io.spine.time.string.TimeStringifiers.forOffsetTime;
import static io.spine.time.string.TimeStringifiers.forTimestamp;
import static io.spine.time.string.TimeStringifiers.forZoneOffset;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alexander Yevsyukov
 */
@DisplayName("TimeStringifiers should")
class TimeStringifiersTest extends UtilityClassTest<TimeStringifiers> {

    TimeStringifiersTest() {
        super(TimeStringifiers.class);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent") // OK since it would break the test if missing.
    private static Stringifier<Object> getStringifier(Class<?> cls) {
        return StringifierRegistry.getInstance().get(cls).get();
    }

    @Test
    @DisplayName("register stringifiers for standard types")
    void registerStringifiers() {
        assertStringifier(ZoneOffset.class, forZoneOffset());
        assertStringifier(Duration.class, forDuration());
        assertStringifier(Timestamp.class, forTimestamp());
        assertStringifier(LocalDate.class, forLocalDate());
        assertStringifier(LocalTime.class, forLocalTime());
        assertStringifier(OffsetDateTime.class, forOffsetDateTime());
        assertStringifier(OffsetTime.class, forOffsetTime());
    }

    private static <T> void assertStringifier(Class<T> dataClass, Stringifier<T> stringifier) {
        Stringifier<?> current = getStringifier(dataClass);
        assertEquals(stringifier, current);
    }
}
