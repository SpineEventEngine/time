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

import io.spine.time.string.TimeStringifiers;
import io.spine.util.SerializableConverter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utilities for working with {@code ZoneId}.
 */
public final class ZoneIds {

    /** Prevents instantiation of this utility class. */
    private ZoneIds() {
    }

    private static ZoneId create(String id) {
        ZoneId result = ZoneId
                .newBuilder()
                .setValue(id)
                .vBuild();
        return result;
    }

    /**
     * Obtains the system default time-zone.
     */
    public static ZoneId systemDefault() {
        String id = java.time.ZoneId.systemDefault()
                                    .getId();
        return create(id);
    }

    /**
     * Obtains the instance for the passed value.
     */
    public static ZoneId of(String id) {
        checkNotNull(id);
        java.time.ZoneId zi = java.time.ZoneId.of(id);
        return converter().convert(zi);
    }

    /**
     * Creates an instance by the passed Java Time value.
     */
    public static ZoneId of(java.time.ZoneId value) {
        checkNotNull(value);
        return converter().convert(value);
    }

    /**
     * Converts the passed value to Java Time.
     */
    public static java.time.ZoneId toJavaTime(ZoneId value) {
        checkNotNull(value);
        return converter().reverse()
                          .convert(value);
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
        return TimeStringifiers.forZoneId()
                               .reverse()
                               .convert(str);
    }

    /**
     * Converts the passed instance to the string representation of the zone ID.
     *
     * @see #parse(String)
     */
    public static String toString(ZoneId value) {
        checkNotNull(value);
        return TimeStringifiers.forZoneId()
                               .convert(value);
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
