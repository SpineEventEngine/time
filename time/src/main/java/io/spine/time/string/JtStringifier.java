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

package io.spine.time.string;

import io.spine.string.SerializableStringifier;
import io.spine.util.SerializableConverter;
import io.spine.util.SerializableFunction;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.util.Exceptions.illegalArgumentWithCauseOf;
import static java.util.Objects.requireNonNull;

/**
 * An abstract base for stringifier that use Java Time types for conversion to string and parsing.
 *
 * @param <T>
 *         the type to stringify
 * @param <J>
 *         the Java Time type which corresponds to type {@code T}
 */
abstract class JtStringifier<T, J> extends SerializableStringifier<T> {

    private static final long serialVersionUID = 0L;

    private final SerializableConverter<J, T> converter;
    private final SerializableFunction<String, J> parser;

    JtStringifier(String identity,
                  SerializableFunction<String, J> parser,
                  SerializableConverter<J, T> converter) {
        super(identity);
        this.converter = checkNotNull(converter);
        this.parser = checkNotNull(parser);
    }

    @Override
    protected String toString(T value) {
        var javaTime = converter.reverse().convert(value);
        requireNonNull(javaTime);

        var result = javaTime.toString();
        return result;
    }

    @Override
    protected T fromString(String str) {
        T value;
        try {
            var parsed = parser.apply(str);
            value = converter.convert(parsed);
        } catch (RuntimeException e) {
            throw illegalArgumentWithCauseOf(e);
        }
        return requireNonNull(value);
    }
}
