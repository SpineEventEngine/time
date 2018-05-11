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

/**
 * Constants related to time as part of International System of Units (SI).
 *
 * @author Mykhailo Drachuk
 */
public class SiTime {

    /** The count of nanoseconds in one second. */
    public static final int NANOS_PER_SECOND = 1_000_000_000;

    /** The count of nanoseconds in one millisecond. */
    public static final int NANOS_PER_MILLISECOND = 1_000_000;

    /** The count of milliseconds in one second. */
    public static final int MILLIS_PER_SECOND = 1000;

    /** The count of nanoseconds in a microsecond. */
    public static final int NANOS_PER_MICROSECOND = 1000;

    /** The count of microseconds in one second. */
    public static final int MICROS_PER_SECOND = 1_000_000;

    /** Prevent instantiation of this utility class. */
    private SiTime() {
        // Does nothing.
    }
}
