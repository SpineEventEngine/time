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

package io.spine.time;

import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import io.spine.base.Time;

import java.time.Instant;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.protobuf.util.Timestamps.compare;
import static io.spine.time.DtPreconditions.checkPeriod;
import static io.spine.time.DtPreconditions.checkSameType;

/**
 * A point in time represented with a certain accuracy.
 *
 * <p>The name of this interface is inspired by the {@link java.time.temporal.Temporal}.
 *
 * <p>Provides a {@linkplain #compareTo(Temporal) default implementation} for comparison of two
 * points in time. It is not supposed that concrete {@code Temporal}s would override this comparison
 * mechanism.
 *
 * @param <T>
 *         the type of itself
 * @apiNote This interface is mainly (though not exclusively) designed to be implemented in
 *         messages
 *         marked with the {@code (is)} option. See {@link TemporalMessage}.
 */
@SuppressWarnings("ClassWithTooManyMethods") // because of convenience overloads.
public interface Temporal<T extends Temporal<T>> extends Comparable<T> {

    /**
     * Converts this {@code Temporal} into a {@link java.time.Instant}.
     *
     * @return this as an {@code Instant}
     */
    Instant toInstant();

    /**
     * Obtains this point in time as a Protobuf {@link Timestamp}.
     *
     * <p>The Protobuf {@code Timestamp} represents the UTC Epoch time. All the implementations
     * should assemble timestamps regarding that fact.
     *
     * <p>If this {@code Temporal} type lacks precision of the {@code Timestamp}, such as seconds,
     * nanoseconds, etc., the smallest possible value of {@code Timestamp} should be returned.
     * For example, if this type represents time up to a minute, the value {@code 01-02-2019 09:40}
     * is translated to the timestamp as if it was {@code 01-02-2019 09:40:00.000000000} (with zero
     * seconds and zero nanoseconds). Similarly, if the type only represents a year, then
     * the obtained timestamp points at the {@code 1st of January 00:00:00} of that year.
     *
     * @return this is a {@code Timestamp}
     */
    default Timestamp toTimestamp() {
        var instant = toInstant();
        return JavaTimeExtensions.toTimestamp(instant);
    }

    /**
     * Packs this point in time into an {@link Any}.
     *
     * @return itself packed as {@code Any}
     */
    Any toAny();

    /**
     * Compares this point in time to the given one.
     *
     * <p>The {@code other} point should have <strong>the exact</strong> runtime type as this one.
     * Otherwise, an {@code IllegalArgumentException} is thrown. The same constraint is applicable
     * to other comparison methods that accept arguments with the type {@code <T>}.
     *
     * @param other
     *         the value to compare to
     * @return <ul>
     *             <li>an integer greater than 0 if point in time occurs later than the other;
     *             <li>an integer less than 0 if point in time occurs earlier than other;
     *             <li>and 0 of these points in time are identical.
     *         </ul>
     * @implNote Translates both temporal values into {@code Timestamp}s and compares them.
     */
    @Override
    default int compareTo(T other) {
        checkNotNull(other);
        checkSameType(this, other);
        var thisTimestamp = toTimestamp();
        var otherTimestamp = other.toTimestamp();
        var result = compare(thisTimestamp, otherTimestamp);
        return result;
    }

    /**
     * Compares this point in time to the given one.
     */
    default int compareTo(Instant other) {
        var thisTimestamp = toTimestamp();
        var otherTimestamp = JavaTimeExtensions.toTimestamp(other);
        var result = compare(thisTimestamp, otherTimestamp);
        return result;
    }

    /**
     * Compares this point in time to the given one.
     */
    default int compareTo(Timestamp other) {
        var thisTimestamp = toTimestamp();
        var result = compare(thisTimestamp, other);
        return result;
    }

    /**
     * Checks if this point is time occurs earlier than the other one.
     *
     * @deprecated please use {@link #isBefore(Temporal)}
     */
    @Deprecated
    default boolean isEarlierThan(T other) {
        return isBefore(other);
    }

    /**
     * Checks if this point is time occurs earlier than the other one.
     */
    default boolean isBefore(T other) {
        return compareTo(other) < 0;
    }

    /**
     * Checks if this point is time occurs earlier than the other one.
     */
    default boolean isBefore(Instant other) {
        return compareTo(other) < 0;
    }

    /**
     * Checks if this point is time occurs earlier than the other one.
     */
    default boolean isBefore(Timestamp other) {
        return compareTo(other) < 0;
    }

    /**
     * Checks if this point is time occurs earlier than the other one or they coincide.
     *
     * @deprecated please use {@link #isBeforeOrSameAs(Temporal)}.
     */
    @Deprecated
    default boolean isEarlierOrSameAs(T other) {
        return isBeforeOrSameAs(other);
    }

    /**
     * Checks if this point is time occurs earlier than the other one or they coincide.
     */
    default boolean isBeforeOrSameAs(T other) {
        return compareTo(other) <= 0;
    }

    /**
     * Checks if this point is time occurs earlier than the other one or they coincide.
     */
    default boolean isBeforeOrSameAs(Instant other) {
        return compareTo(other) <= 0;
    }

    /**
     * Checks if this point is time occurs earlier than the other one or they coincide.
     */
    default boolean isBeforeOrSameAs(Timestamp other) {
        return compareTo(other) <= 0;
    }

    /**
     * Checks if this point in time coincides with the given one.
     */
    default boolean isSameAs(T other) {
        return compareTo(other) == 0;
    }

    /**
     * Checks if this point in time coincides with the given one.
     */
    default boolean isSameAs(Instant other) {
        return compareTo(other) == 0;
    }

    /**
     * Checks if this point in time coincides with the given one.
     */
    default boolean isSameAs(Timestamp other) {
        return compareTo(other) == 0;
    }

    /**
     * Checks if this point is time occurs later than the other one.
     *
     * @deprecated please use {@link #isAfter(Temporal)}.
     */
    @Deprecated
    default boolean isLaterThan(T other) {
        return isAfter(other);
    }

    /**
     * Checks if this point is time occurs later than the other one.
     */
    default boolean isAfter(T other) {
        return compareTo(other) > 0;
    }

    /**
     * Checks if this point is time occurs later than the other one.
     */
    default boolean isAfter(Instant other) {
        return compareTo(other) > 0;
    }

    /**
     * Checks if this point is time occurs later than the other one.
     */
    default boolean isAfter(Timestamp other) {
        return compareTo(other) > 0;
    }

    /**
     * Checks if this point is time occurs later than the other one or they coincide.
     *
     * @deprecated please use {@link #isAfterOrSameAs(Temporal)}.
     */
    @Deprecated
    default boolean isLaterOrSameAs(T other) {
        return isAfterOrSameAs(other);
    }

    /**
     * Checks if this point is time occurs later than the other one or they coincide.
     */
    default boolean isAfterOrSameAs(T other) {
        return compareTo(other) >= 0;
    }

    /**
     * Checks if this point is time occurs later than the other one or they coincide.
     */
    default boolean isAfterOrSameAs(Instant other) {
        return compareTo(other) >= 0;
    }

    /**
     * Checks if this point is time occurs later than the other one or they coincide.
     */
    default boolean isAfterOrSameAs(Timestamp other) {
        return compareTo(other) >= 0;
    }

    /**
     * Checks if this point is time lies between the given.
     *
     * <p>All three {@code Temporal}s must exactly the same runtime type. Otherwise,
     * an {@code IllegalArgumentException} is thrown.
     *
     * @param periodStart
     *         lower bound, exclusive
     * @param periodEnd
     *         higher bound, inclusive
     * @return {@code true} if this point in time lies in between the given two
     */
    default boolean isBetween(T periodStart, T periodEnd) {
        checkPeriod(periodStart, periodEnd);
        return isAfter(periodStart)
                && isBeforeOrSameAs(periodEnd);
    }


    /**
     * Checks if this point is time lies between the given.
     *
     * @param periodStart
     *         lower bound, exclusive
     * @param periodEnd
     *         higher bound, inclusive
     * @return {@code true} if this point in time lies in between the given two
     */
    default boolean isBetween(Instant periodStart, Instant periodEnd) {
        checkPeriod(periodStart, periodEnd);
        return isAfter(periodStart)
                && isBeforeOrSameAs(periodEnd);
    }

    /**
     * Checks if this point is time lies between the given.
     *
     * @param periodStart
     *         lower bound, exclusive
     * @param periodEnd
     *         higher bound, inclusive
     * @return {@code true} if this point in time lies in between the given two
     */
    default boolean isBetween(Timestamp periodStart, Timestamp periodEnd) {
        checkPeriod(periodStart, periodEnd);
        return isAfter(periodStart)
                && isBeforeOrSameAs(periodEnd);
    }

    /**
     * Checks that this point in time lies in the future.
     *
     * <p>Uses {@link Time#currentTime()} to determine the "current" time to compare to.
     *
     * @return {@code true} if this point is time is later than the current time,
     *         {@code false} otherwise
     * @apiNote Note that a point in time is considered to be in the future only if its
     *         {@linkplain #toTimestamp() timestamp representation} is in the future.
     */
    default boolean isInFuture() {
        var now = Time.currentTime();
        var thisTime = toTimestamp();
        return compare(thisTime, now) > 0;
    }

    /**
     * Checks that this point in time lies in the past.
     *
     * <p>Uses {@link Time#currentTime()} to determine the "current" time to compare to.
     *
     * @return {@code true} if this point is time is earlier than the current time,
     *         {@code false} otherwise
     * @apiNote Note that a point in time is considered to be in the past only if its
     *         {@linkplain #toTimestamp() timestamp representation} is in the past. Thus, if a type
     *         representing time with precision with up to a year contains current year, it is
     *         considered to be in the past, as the year has already started.
     */
    default boolean isInPast() {
        var now = Time.currentTime();
        var thisTime = toTimestamp();
        return compare(thisTime, now) < 0;
    }
}
