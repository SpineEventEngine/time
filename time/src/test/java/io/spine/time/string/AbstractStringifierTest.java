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

import io.spine.string.Stringifier;
import io.spine.string.StringifierRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static com.google.common.testing.SerializableTester.reserializeAndAssert;
import static com.google.common.truth.Truth8.assertThat;
import static io.spine.testing.Assertions.assertHasPrivateParameterlessCtor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The abstract base for stringifier tests.
 *
 * @param <T>
 *         the type of stringifier objects
 */
abstract class AbstractStringifierTest<T> {

    private final Stringifier<T> stringifier;
    private final Class<T> dataClass;

    AbstractStringifierTest(Stringifier<T> stringifier, Class<T> dataClass) {
        this.stringifier = stringifier;
        this.dataClass = dataClass;
    }

    protected abstract T createObject();

    Stringifier<T> stringifier() {
        return stringifier;
    }

    @Test
    @DisplayName("have private singleton constructor")
    void privateCtor() {
        assertHasPrivateParameterlessCtor(stringifier().getClass());
    }

    @Test
    @DisplayName("convert forward and backward")
    void convert() {
        var obj = createObject();

        final var str = stringifier.convert(obj);
        final var convertedBack = stringifier.reverse().convert(str);

        assertEquals(obj, convertedBack);
    }

    @Test
    @DisplayName("prohibit empty string input")
    void prohibitEmptyString() {
        assertThrows(
                IllegalArgumentException.class,
                () -> stringifier.reverse()
                                 .convert("")
        );
    }

    @Test
    @DisplayName("serialize")
    void serialize() {
        var expected = stringifier();
        var stringifier = reserializeAndAssert(expected);
        assertSame(expected, stringifier);
    }

    @Test
    @DisplayName("be registered")
    void isRegistered() {
        var registry = StringifierRegistry.instance();
        assertThat(registry.find(dataClass))
                .isPresent();
    }

    @Test
    @DisplayName("be a `final` class")
    void isFinalClass() {
        assertTrue(Modifier.isFinal(stringifier.getClass().getModifiers()));
    }
}
