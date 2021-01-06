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

package io.spine.time;

import com.google.common.base.Converter;
import com.google.common.testing.NullPointerTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static com.google.common.testing.SerializableTester.reserializeAndAssert;
import static io.spine.testing.DisplayNames.NOT_ACCEPT_NULLS;
import static io.spine.testing.Tests.assertHasPrivateParameterlessCtor;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Abstract base for tests of date-time utility classes.
 *
 * @param <T> the type with which the utility class work
 * @param <J> the Java Type which corresponds to the type {@code <T>}
 */
abstract class AbstractDateTimeUtilityTest<T, J> {

    private final Class<?> utilityClass;
    private final Converter<J, T> converter;
    private final Function<Now, T> current;
    private final Function<T, String> strOut;
    private final Function<String, T> parser;

    /**
     * Creates new test suite.
     *
     * @param utilityClass the utility class to test
     * @param current      the supplier value of the data type at the current time or location
     *                     (e.g. for {@code ZoneOffset} or {@code ZoneId}).
     *                     It could be method reference of the utility class, or another supplier
     *                     for such values if they are available elsewhere.
     * @param strOut       a reference to a string output method
     * @param parser       a reference to a parsing method
     * @param converter    a converter from/to Java Time
     */
    AbstractDateTimeUtilityTest(Class<?> utilityClass,
                                Function<Now, T> current,
                                Function<T, String> strOut,
                                Function<String, T> parser,
                                Converter<J, T> converter) {
        this.utilityClass = utilityClass;
        this.current = current;
        this.strOut = strOut;
        this.parser = parser;
        this.converter = converter;
    }

    abstract void addDefaults(NullPointerTester nullTester);

    T current() {
        return current.apply(Now.get());
    }

    @Test
    @DisplayName("have utility constructor")
    void haveUtilityConstructor() {
        assertHasPrivateParameterlessCtor(utilityClass);
    }

    @Test
    @DisplayName(NOT_ACCEPT_NULLS)
    void rejectNulls() {
        NullPointerTester tester = new NullPointerTester();
        addDefaults(tester);
        tester.testAllPublicStaticMethods(utilityClass);
    }

    @Test
    @DisplayName("convert to String and parse back")
    void toFromString() {
        T expected = current();
        String str = strOut.apply(expected);
        T converted = parser.apply(str);
        assertEquals(expected, converted);
    }

    @Test
    @DisplayName("convert to Java Time and back")
    void toFromJavaTime() {
        T expected = current();
        J converted = converter.reverse()
                               .convert(expected);
        T back = converter.convert(converted);
        assertEquals(expected, back);
    }

    @Test
    @DisplayName("provide serializable Converter from/to Java Time")
    void converter() {
        reserializeAndAssert(converter);
    }
}
