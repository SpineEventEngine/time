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

@file:JvmName("DurationExtensions")

package io.spine.time

import com.google.protobuf.Duration
import com.google.protobuf.util.Durations.compare
import com.google.protobuf.util.Durations.isNegative
import com.google.protobuf.util.Durations.isValid
import com.google.protobuf.util.Durations.toDays
import com.google.protobuf.util.Durations.toHours
import com.google.protobuf.util.Durations.toMicros
import com.google.protobuf.util.Durations.toMillis
import com.google.protobuf.util.Durations.toMinutes
import com.google.protobuf.util.Durations.toNanos
import com.google.protobuf.util.Durations.toSeconds
import com.google.protobuf.util.Durations.toString
import io.spine.protobuf.Durations2
import io.spine.protobuf.Durations2.add
import io.spine.protobuf.Durations2.isPositive
import io.spine.protobuf.Durations2.isPositiveOrZero
import io.spine.protobuf.Durations2.isZero
import io.spine.protobuf.Durations2.toJavaTime

/**
 * Compares this duration with the passed one.
 *
 * @return the value `0` if `x == y`}`; a value less than `0`}` if `x < y`;
 *     and a value greater than `0` if `x > y`.
 */
public operator fun Duration.compareTo(other: Duration): Int = compare(this, other)

/**
 * Returns true if this duration is valid.
 *
 * The [seconds][Duration.getSeconds] value must be in the range
 * [-315,576,000,000, +315,576,000,000].
 *
 * The [nanos][Duration.getNanos] value must be in the range [-999,999,999, +999,999,999].
 *
 * **Note:** Durations less than one second are represented with a zero
 * `seconds` field and a positive or negative `nanos` field. For durations of one second or more,
 * a non-zero value for the `nanos` field must be of the same sign as the `seconds` field.
 */
public fun Duration.isValid(): Boolean = isValid(this)

/**
 * Tells whether this duration is negative or not.
 */
public fun Duration.isNegative(): Boolean = isNegative(this)

/**
 * Tells whether this duration is positive or not.
 *
 * @returns `true` if the passed value is greater than zero, `false` otherwise.
 */
public fun Duration.isPositive(): Boolean = isPositive(this)

/**
 * Tells whether this duration is not negative.
 *
 * @returns `true` of the passed value is greater or equal zero, `false` otherwise.
 */
public fun Duration.isPositiveOrZero(): Boolean = isPositiveOrZero(this)

/**
 * Returns `true` if the passed value is zero, `false` otherwise.
 *
 * @see Durations2.ZERO
 */
public fun Duration.isZero(): Boolean = isZero(this)

/**
 * Convert Duration to string format.
 *
 * The string format will contains 3, 6, or 9 fractional digits depending on the precision required
 * to represent the exact `Duration` value. For example: "1s", "1.010s", "1.000000100s", "-3.100s".
 * The range that can be represented by `Duration` is from -315,576,000,000 to +315,576,000,000
 * inclusive (in seconds).
 *
 * @return The string representation of the given duration.
 * @throws IllegalArgumentException
 *          if the given duration is not in the valid range.
 */
public fun Duration.print(): String = toString(this)

/**
 * Convert this duration to the number of days.
 *
 * @return the number of days rounded towards 0 to the nearest day.
 */
public fun Duration.toDays(): Long = toDays(this)

/**
 * Convert this duration to the number of hours.
 *
 * @return the number of hours rounded towards 0 to the nearest hour.
 */
public fun Duration.toHours(): Long = toHours(this)

/**
 * Convert this duration to the number of minutes.
 *
 * @return the number of minutes rounded towards 0 to the nearest minute.
 */
public fun Duration.toMinutes(): Long = toMinutes(this)

/**
 * Convert this duration to the number of seconds.
 *
 * @return the number of seconds rounded towards 0 to the nearest second.
 *         E.g., if the duration represents -1 nanosecond, it will be rounded to 0.
 */
public fun Duration.toSeconds(): Long = toSeconds(this);

/**
 * Convert this duration to the number of milliseconds.
 *
 * @return the number of milliseconds rounded towards 0 to the nearest millisecond.
 *         E.g., if the duration represents -1 nanosecond, it will be rounded to 0.
 */
public fun Duration.toMillis(): Long = toMillis(this)

/**
 * Convert this duration to the number of microseconds.
 *
 * @return the number of microseconds rounded towards 0 to the nearest microseconds.
 *         E.g., if the duration represents -1 nanosecond, it will be rounded to 0.
 */
public fun Duration.toMicros(): Long = toMicros(this)

/**
 * Converts this duration to the number of nanoseconds.
 */
public fun Duration.toNanos(): Long = toNanos(this)

/**
 * Converts this duration to Java Time instance.
 */
public fun Duration.toJavaTime(): java.time.Duration = toJavaTime(this);

/**
 * Adds the passed duration to this one.
 *
 * @return
 * - Sum of two durations if both of them are non-null.
 * - Another `non-null` value, if one is `null`.
 * - [ZERO][Durations2.ZERO] if both values are `null`.
 */
public operator fun Duration.plus(other: Duration?): Duration = add(this, other)
