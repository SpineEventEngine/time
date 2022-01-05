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

import io.spine.time.string.TimeStringifiers;
import io.spine.util.SerializableConverter;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

/**
 * Utilities for working with {@code ZoneId}.
 */
public final class ZoneIds {

    /** Prevents instantiation of this utility class. */
    private ZoneIds() {
    }

    private static ZoneId create(String id) {
        var result = ZoneId.newBuilder()
                .setValue(id)
                .vBuild();
        return result;
    }

    /**
     * Obtains the system default time-zone.
     */
    public static ZoneId systemDefault() {
        var id = java.time.ZoneId.systemDefault().getId();
        return create(id);
    }

    /**
     * Obtains the instance for the passed value.
     */
    public static ZoneId of(String id) {
        checkNotNull(id);
        var zi = java.time.ZoneId.of(id);
        var result = converter().convert(zi);
        return requireNonNull(result);
    }

    /**
     * Creates an instance by the passed Java Time value.
     */
    public static ZoneId of(java.time.ZoneId value) {
        checkNotNull(value);
        var result = converter().convert(value);
        return requireNonNull(result);
    }

    /**
     * Converts the passed value to Java Time.
     *
     * @deprecated please use {@link ZoneId#toJavaTime()} instead.
     */
    @Deprecated
    public static java.time.ZoneId toJavaTime(ZoneId value) {
        checkNotNull(value);
        return value.toJavaTime();
    }

    /**
     * Parses zone ID from the passed string, ensuring that the ID is valid and is
     * available for use.
     *
     * @see #toString(ZoneId)
     * @see java.time.ZoneId#of(String)
     */
    public static ZoneId parse(String str) {
        checkNotNull(str);
        var converter = TimeStringifiers.forZoneId().reverse();
        var result = converter.convert(str);
        return requireNonNull(result);
    }

    /**
     * Converts the passed instance to the string representation of the zone ID.
     *
     * @see #parse(String)
     */
    public static String toString(ZoneId value) {
        checkNotNull(value);
        var result = TimeStringifiers.forZoneId().convert(value);
        return requireNonNull(result);
    }

    /**
     * Obtains the converter from Java Time and back.
     */
    public static SerializableConverter<java.time.ZoneId, ZoneId> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java Time and back.
     */
    private static final class JtConverter extends AbstractConverter<java.time.ZoneId, ZoneId> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        private JtConverter() {
            super("ZoneIds.converter()");
        }

        @Override
        protected ZoneId doForward(java.time.ZoneId id) {
            return create(id.getId());
        }

        @Override
        protected java.time.ZoneId doBackward(ZoneId id) {
            return java.time.ZoneId.of(id.getValue());
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
