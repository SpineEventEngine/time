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

import java.util.concurrent.TimeUnit;

/**
 * Constants related to time as part of International System of Units (SI).
 */
@SuppressWarnings("NumericCastThatLosesPrecision")
final class Constants {

    /** The count of nanoseconds in one second. */
    static final int NANOS_PER_SECOND = (int) TimeUnit.SECONDS.toNanos(1);

    /** The count of milliseconds in one second. */
    static final int MILLIS_PER_SECOND = (int) TimeUnit.SECONDS.toMillis(1);

    /** The count of seconds in one minute. */
    static final int SECONDS_PER_MINUTE = (int) TimeUnit.MINUTES.toSeconds(1);

    /** The count of minutes in one hour. */
    static final int MINUTES_PER_HOUR = (int) TimeUnit.HOURS.toMinutes(1);

    /** The count of hours per day. */
    static final int HOURS_PER_DAY = (int) TimeUnit.DAYS.toHours(1);

    /** Prevent instantiation of this utility class. */
    private Constants() {
    }
}
