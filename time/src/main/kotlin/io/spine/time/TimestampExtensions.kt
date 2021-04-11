/*
 * Copyright 2021, TeamDev. All rights reserved.
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

@file:JvmName("TimestampExtensions")

package io.spine.time

import com.google.protobuf.Duration
import com.google.protobuf.Timestamp
import com.google.protobuf.util.Timestamps.add
import com.google.protobuf.util.Timestamps.between
import com.google.protobuf.util.Timestamps.compare
import com.google.protobuf.util.Timestamps.isValid
import com.google.protobuf.util.Timestamps.subtract
import com.google.protobuf.util.Timestamps.toMicros
import com.google.protobuf.util.Timestamps.toMillis
import com.google.protobuf.util.Timestamps.toNanos
import com.google.protobuf.util.Timestamps.toString

/**
 * Compares this timestamp with the passed one.
 *
 * @return
 * - the value `0` if `this == other`;
 * - a value less than `0` if `this < other`;
 * - a value greater than `0` if `this > other`.
 */
public operator fun Timestamp.compareTo(other: Timestamp): Int = compare(this, other)

/**
 * Verifies whether this point in time is before the passed one.
 */
public fun Timestamp.isBefore(other: Timestamp): Boolean = this < other

/**
 * Verifies whether this point in time is after the passed one.
 */
public fun Timestamp.isAfter(other: Timestamp): Boolean = this > other

/**
 * Checks if this point is time lies between the given.
 *
 * @param periodStart
 *         lower bound, exclusive
 * @param periodEnd
 *         higher bound, inclusive
 * @return `true` if this point in time lies in between the given two, `false` otherwise
 */
public fun Timestamp.isBetween(periodStart: Timestamp, periodEnd: Timestamp): Boolean =
    this > periodStart && this <= periodEnd

/**
 * Returns true if this instance is valid.
 *
 * The [seconds][Timestamp.getSeconds] value must be in the range
 * `[-62,135,596,800, +253,402,300,799]` (i.e., between `0001-01-01T00:00:00Z` and
 * `9999-12-31T23:59:59Z`).
 *
 * The [nanos][Timestamp.getNanos] value must be in the range `[0, +999,999,999]`.
 *
 * **Note:** Negative second values with fractional seconds must still have non-negative
 * nanos values that count forward in time.
 */
public fun Timestamp.isValid(): Boolean = isValid(this)

/**
 * Converts this timestamp to RFC 3339 date string format.
 *
 * The output will always be Z-normalized and uses 3, 6 or 9 fractional digits as required to
 * represent the exact value.
 *
 * Note that `Timestamp` can only represent time from `0001-01-01T00:00:00Z` to
 * `9999-12-31T23:59:59.999999999Z`. Please see [RFC3339](https://www.ietf.org/rfc/rfc3339.txt)
 * for details.
 *
 * Example of generated format: `"1972-01-01T10:00:20.021Z"`.
 *
 * @return The string representation of the given timestamp.
 * @throws IllegalArgumentException
 *          if the given timestamp is not in the valid range.
 */
public fun Timestamp.print(): String = toString(this)

/**
 * Converts this timestamp to the number of milliseconds elapsed from the epoch.
 *
 * The result will be rounded down to the nearest millisecond.
 * E.g., if the timestamp represents `"1969-12-31T23:59:59.999999999Z"`,
 * it will be rounded to -1 millisecond.
 */
public fun Timestamp.toMillis(): Long = toMillis(this)

/**
 * Converts this timestamp to the number of microseconds elapsed from the epoch.
 *
 * The result will be rounded down to the nearest microsecond.
 * E.g., if the timestamp represents `"1969-12-31T23:59:59.999999999Z"`,
 * it will be rounded to -1 microsecond.
 */
public fun Timestamp.toMicros(): Long = toMicros(this)

/**
 * Converts this timestamp to the number of nanoseconds elapsed from the epoch.
 */
public fun Timestamp.toNanos(): Long = toNanos(this)

/**
 * Adds a duration to this timestamp.
 */
public operator fun Timestamp.plus(length: Duration): Timestamp = add(this, length)

/**
 * Calculates the distance between this timestamp and the passed one.
 *
 * @return
 * - positive duration if `this < other`,
 * - negative duration if `this > other`, and
 * - zero if `this == other`.
 */
public operator fun Timestamp.minus(other: Timestamp): Duration = between(this, other)

/**
 * Subtracts a duration from this timestamp.
 */
public operator fun Timestamp.minus(length: Duration): Timestamp = subtract(this, length)
