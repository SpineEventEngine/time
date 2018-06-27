/*
 * Copyright (c) 2000-2015 TeamDev. All rights reserved.
 * TeamDev PROPRIETARY and CONFIDENTIAL.
 * Use is subject to license terms.
 */
package io.spine.time;

import com.google.common.base.Converter;
import com.google.protobuf.Duration;
import com.google.protobuf.util.Durations;
import io.spine.time.string.TimeStringifiers;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.protobuf.util.Durations.compare;
import static com.google.protobuf.util.Durations.fromMillis;
import static com.google.protobuf.util.Durations.fromNanos;
import static com.google.protobuf.util.Durations.fromSeconds;
import static com.google.protobuf.util.Durations.toMillis;
import static io.spine.time.Constants.MINUTES_PER_HOUR;
import static io.spine.time.Constants.SECONDS_PER_MINUTE;
import static io.spine.time.Constants.MILLIS_PER_SECOND;
import static io.spine.util.Math.floorDiv;
import static io.spine.util.Math.safeMultiply;

/**
 * Utility class for working with durations in addition to those available from the
 * {@link com.google.protobuf.util.Durations Durations} class in the Protobuf Util library.
 *
 * <p>Use {@code import static io.spine.protobuf.Durations2.*} for compact initialization
 * like this:
 * <pre>
 *      Duration d = add(hours(2), minutes(30));
 * </pre>
 *
 * @author Alexander Yevsyukov
 * @see com.google.protobuf.util.Durations Durations
 */
@SuppressWarnings({"UtilityClass", "ClassWithTooManyMethods"})
public final class Durations2 {

    public static final Duration ZERO = fromMillis(0L);

    /** Prevent instantiation of this utility class. */
    private Durations2() {
    }

    /**
     * Obtains an instance of {@code Duration} representing the passed number of minutes.
     *
     * @param minutes the number of minutes, positive or negative.
     * @return a non-null {@code Duration}
     */
    public static Duration fromMinutes(long minutes) {
        Duration duration = fromSeconds(safeMultiply(minutes, SECONDS_PER_MINUTE));
        return duration;
    }

    /**
     * Obtains an instance of {@code Duration} representing the passed number of hours.
     *
     * @param hours the number of hours, positive or negative
     * @return a non-null {@code Duration}
     */
    public static Duration fromHours(long hours) {
        Duration duration = fromMinutes(safeMultiply(hours, MINUTES_PER_HOUR));
        return duration;
    }

    /*
     * Methods for brief computations with Durations like
     *       add(hours(2), minutes(30));
     ******************************************************/

    /**
     * Obtains an instance of {@code Duration} representing the passed number of nanoseconds.
     *
     * @param nanos the number of nanoseconds, positive or negative
     * @return a non-null {@code Duration}
     */
    public static Duration nanos(long nanos) {
        Duration duration = fromNanos(nanos);
        return duration;
    }

    /**
     * Obtains an instance of {@code Duration} representing the passed number of milliseconds.
     *
     * @param milliseconds the number of milliseconds, positive or negative
     * @return a non-null {@code Duration}
     */
    public static Duration milliseconds(long milliseconds) {
        return fromMillis(milliseconds);
    }

    /**
     * Obtains an instance of {@code Duration} representing the passed number of seconds.
     *
     * @param seconds the number of seconds, positive or negative
     * @return a non-null {@code Duration}
     */
    public static Duration seconds(long seconds) {
        return fromSeconds(seconds);
    }

    /**
     * This method allows for more compact code of creation of
     * {@code Duration} instance with minutes.
     */
    public static Duration minutes(long minutes) {
        return fromMinutes(minutes);
    }

    /**
     * This method allows for more compact code of creation of
     * {@code Duration} instance with hours.
     */
    public static Duration hours(long hours) {
        return fromHours(hours);
    }

    /**
     * Adds two durations one of which or both can be {@code null}.
     *
     * <p>This method supplements the {@linkplain Durations#add(Duration, Duration) utility}
     * from Protobuf Utils for accepting {@code null}s.
     *
     * @param d1 a duration to add, could be {@code null}
     * @param d2 another duration to add, could be {@code null}
     * @return <ul>
     * <li>sum of two durations if both of them are {@code non-null}
     * <li>another {@code non-null} value, if one is {@code null}
     * <li>{@link #ZERO} if both values are {@code null}
     * </ul>
     * @see Durations#add(Duration, Duration)
     */
    public static Duration add(@Nullable Duration d1, @Nullable Duration d2) {
        if (d1 == null && d2 == null) {
            return ZERO;
        }
        if (d1 == null) {
            return d2;
        }
        if (d2 == null) {
            return d1;
        }
        Duration result = Durations.add(d1, d2);
        return result;
    }

    /**
     * This method allows for more compact code of creation of
     * {@code Duration} instance with hours and minutes.
     */
    public static Duration hoursAndMinutes(long hours, long minutes) {
        Duration result = add(hours(hours), minutes(minutes));
        return result;
    }

    /** Convert a duration to the number of nanoseconds. */
    public static long toNanos(Duration duration) {
        /* The sole purpose of this method is minimize the dependencies of the classes
           working with durations. */
        checkNotNull(duration);
        long result = Durations.toNanos(duration);
        return result;
    }

    /** Convert a duration to the number of seconds. */
    public static long toSeconds(Duration duration) {
        checkNotNull(duration);
        long millis = toMillis(duration);
        long seconds = floorDiv(millis, MILLIS_PER_SECOND);
        return seconds;
    }

    /**
     * Converts passed duration to long value of minutes.
     *
     * @param duration a duration to convert
     * @return duration in minutes
     */
    public static long toMinutes(Duration duration) {
        checkNotNull(duration);
        long millis = toMillis(duration);
        long result = (millis / MILLIS_PER_SECOND) / SECONDS_PER_MINUTE;
        return result;
    }

    /**
     * Returns the number of hours in the passed duration.
     *
     * @param value duration
     * @return number of hours
     */
    public static long getHours(Duration value) {
        checkNotNull(value);
        long hours = toMinutes(value);
        long result = hours / MINUTES_PER_HOUR;
        return result;
    }

    /**
     * Returns the only remainder of minutes from the passed duration subtracting
     * the amount of full hours.
     *
     * @param value duration
     * @return number of minutes
     */
    public static int getMinutes(Duration value) {
        checkNotNull(value);
        long allMinutes = toMinutes(value);
        long remainder = allMinutes % MINUTES_PER_HOUR;
        int result = Long.valueOf(remainder)
                         .intValue();
        return result;
    }

    /**
     * Returns {@code true} of the passed value is greater or equal zero,
     * {@code false} otherwise.
     */
    public static boolean isPositiveOrZero(Duration value) {
        checkNotNull(value);
        long millis = toMillis(value);
        boolean result = millis >= 0;
        return result;
    }

    /**
     * Returns {@code true} if the passed value is greater than zero,
     * {@code false} otherwise.
     */
    public static boolean isPositive(Duration value) {
        checkNotNull(value);
        boolean secondsPositive = value.getSeconds() > 0;
        boolean nanosPositive = value.getNanos() > 0;
        boolean result = secondsPositive || nanosPositive;
        return result;

    }

    /** Returns {@code true} if the passed value is zero, {@code false} otherwise. */
    public static boolean isZero(Duration value) {
        checkNotNull(value);
        boolean noSeconds = value.getSeconds() == 0;
        boolean noNanos = value.getNanos() == 0;
        boolean result = noSeconds && noNanos;
        return result;
    }

    /**
     * Returns {@code true} if the first argument is greater than the second,
     * {@code false} otherwise.
     */
    public static boolean isGreaterThan(Duration value, Duration another) {
        boolean result = compare(value, another) > 0;
        return result;
    }

    /**
     * Returns {@code true} if the first argument is less than the second,
     * {@code false} otherwise.
     */
    public static boolean isLessThan(Duration value, Duration another) {
        boolean result = compare(value, another) < 0;
        return result;
    }

    /** Returns {@code true} if the passed duration is negative, {@code false} otherwise. */
    public static boolean isNegative(Duration value) {
        checkNotNull(value);
        long nanos = toNanos(value);
        boolean isNegative = nanos < 0;
        return isNegative;
    }

    /**
     * Converts the passed Java Time value.
     */
    public static Duration of(java.time.Duration value) {
        checkNotNull(value);
        return converter().convert(value);
    }

    /**
     * Converts the passed value to Java Time value.
     */
    public static java.time.Duration toJavaTime(Duration value) {
        checkNotNull(value);
        return converter().reverse()
                          .convert(value);
    }

    /**
     * Parses the string with a duration.
     *
     * <p>Unlike {@link com.google.protobuf.util.Durations#parse(String) its Protobuf counterpart}
     * this method does not throw a checked exception.
     *
     * @throws IllegalArgumentException if the string is not of required format
     */
    public static Duration parse(String str) {
        checkNotNull(str);
        return TimeStringifiers.forDuration()
                               .reverse()
                               .convert(str);
    }

    /**
     * Obtains the instance of Java Time converter.
     */
    public static Converter<java.time.Duration, Duration> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java Time {@code Duration} to Protobuf {@code Duration} and back.
     */
    private static final class JtConverter extends AbstractConverter<java.time.Duration, Duration> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        @Override
        protected Duration doForward(java.time.Duration duration) {
            Duration.Builder result = Duration
                    .newBuilder()
                    .setSeconds(duration.getSeconds())
                    .setNanos(duration.getNano());
            return result.build();
        }

        @Override
        protected java.time.Duration doBackward(Duration duration) {
            java.time.Duration result = java.time.Duration
                    .ofSeconds(duration.getSeconds(), duration.getNanos());
            return result;
        }

        @Override
        public String toString() {
            return "Durations2.converter()";
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
