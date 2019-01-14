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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Assertion utilities for date/time tests.
 */
class Asserts {

    /** Prevents instantiation of this utility class. */
    private Asserts() {
    }

    static void assertTimesEqual(java.time.LocalTime jt, LocalTime lt) {
        assertEquals(jt.getHour(), lt.getHour());
        assertEquals(jt.getMinute(), lt.getMinute());
        assertEquals(jt.getSecond(), lt.getSecond());
        assertEquals(jt.getNano(), lt.getNano());
    }

    static void assertDatesEqual(java.time.LocalDate jt, LocalDate ld) {
        assertEquals(jt.getYear(), ld.getYear());
        assertEquals(jt.getMonthValue(), ld.getMonth()
                                           .getNumber());
        assertEquals(jt.getDayOfMonth(), ld.getDay());
    }
}
