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

package io.spine.time.string;

import com.google.common.base.Converter;
import io.spine.string.SerializableStringifier;
import io.spine.util.SerializableFunction;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.util.Exceptions.illegalArgumentWithCauseOf;

/**
 * An abstract base for stringifier that use Java Time types for conversion to string and parsing.
 *
 * @param <T> the type to stringify
 * @param <J> the Java Time type which corresponds to type {@code T}
 */
abstract class JtStringifier<T, J> extends SerializableStringifier<T> {

    private static final long serialVersionUID = 0L;

    @SuppressWarnings("NonSerializableFieldInSerializableClass") /* All converters
        passed to JtStringifier are internal to Spine Time and are Serializable. */
    private final Converter<J, T> converter;
    private final SerializableFunction<String, J> parser;

    JtStringifier(String identity,
                  SerializableFunction<String, J> parser,
                  Converter<J, T> converter) {
        super(identity);
        this.converter = checkNotNull(converter);
        this.parser = checkNotNull(parser);
    }

    @Override
    protected String toString(T value) {
        J javaTime = converter.reverse()
                              .convert(value);
        checkNotNull(javaTime);
        String result = javaTime.toString();
        return result;
    }

    @Override
    protected T fromString(String str) {
        T value;
        try {
            J parsed = parser.apply(str);
            value = converter.convert(parsed);
        } catch (RuntimeException e) {
            throw illegalArgumentWithCauseOf(e);
        }
        return value;
    }
}
