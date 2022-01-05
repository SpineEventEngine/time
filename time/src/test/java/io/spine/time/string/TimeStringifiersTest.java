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

package io.spine.time.string;

import com.google.common.truth.Truth8;
import io.spine.string.Stringifier;
import io.spine.string.StringifierRegistry;
import io.spine.testing.UtilityClassTest;
import io.spine.time.LocalDate;
import io.spine.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.time.string.TimeStringifiers.forLocalDate;
import static io.spine.time.string.TimeStringifiers.forLocalTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("`TimeStringifiers` should")
class TimeStringifiersTest extends UtilityClassTest<TimeStringifiers> {

    TimeStringifiersTest() {
        super(TimeStringifiers.class);
    }

    @Test
    @DisplayName("register stringifiers for standard types")
    void registerStringifiers() {
        assertStringifier(LocalDate.class, forLocalDate());
        assertStringifier(LocalTime.class, forLocalTime());

        assertDeprecatedStringifiersToo();
    }

    @SuppressWarnings("deprecation")
    private static void assertDeprecatedStringifiersToo() {
        assertStringifier(io.spine.time.ZoneOffset.class,
                          TimeStringifiers.forZoneOffset());
        assertStringifier(io.spine.time.OffsetDateTime.class,
                          TimeStringifiers.forOffsetDateTime());
        assertStringifier(io.spine.time.OffsetTime.class,
                          TimeStringifiers.forOffsetTime());
    }

    private static <T> void assertStringifier(Class<T> dataClass, Stringifier<T> stringifier) {
        Stringifier<?> current = stringifierOf(dataClass);
        assertEquals(stringifier, current);
    }

    private static Stringifier<Object> stringifierOf(Class<?> cls) {
        var stringifier = StringifierRegistry.instance().find(cls);
        Truth8.assertThat(stringifier).isPresent();
        return stringifier.get();
    }
}
