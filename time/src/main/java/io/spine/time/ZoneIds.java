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

import com.google.common.base.Converter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utilities for working with {@code ZoneId}.
 *
 * @author Alexander Yevsyukov
 */
public class ZoneIds {

    /** Prevents instantiation of this utility class. */
    private ZoneIds() {
    }

    private static ZoneId create(String id) {
        ZoneId.Builder result = ZoneId
                .newBuilder()
                .setValue(id);
        return result.build();
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
     * Obtains the converter from Java Time and back.
     */
    public static Converter<java.time.ZoneId, ZoneId> converter() {
        return JtConverter.INSTANCE;
    }

    /**
     * Converts from Java Time and back.
     */
    private static final class JtConverter extends AbstractConverter<java.time.ZoneId, ZoneId> {

        private static final long serialVersionUID = 0L;
        private static final JtConverter INSTANCE = new JtConverter();

        @Override
        protected ZoneId doForward(java.time.ZoneId id) {
            return create(id.getId());
        }

        @Override
        protected java.time.ZoneId doBackward(ZoneId id) {
            return java.time.ZoneId.of(id.getValue());
        }

        @Override
        public String toString() {
            return "ZoneIds.converter()";
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}