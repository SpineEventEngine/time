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
import com.google.common.testing.NullPointerTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.testing.SerializableTester.reserializeAndAssert;
import static io.spine.test.DisplayNames.HAVE_PARAMETERLESS_CTOR;
import static io.spine.test.DisplayNames.NOT_ACCEPT_NULLS;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;

/**
 * Abstract base for tests of date-time utility classes.
 *
 * @param <T> the type with which the utility class work
 * @param <J> the Java Type which corresponds to the type {@code <T>}
 * @author Alexander Yevsyukov
 */
abstract class AbstractDateTimeUtilityTest<T, J> {

    private final Class<?> utilityClass;
    private final Converter<J, T> converter;

    AbstractDateTimeUtilityTest(Class<?> aClass, Converter<J, T> converter) {
        this.utilityClass = aClass;
        this.converter = converter;
    }

    abstract void addDefaults(NullPointerTester nullTester);

    @Test
    @DisplayName(HAVE_PARAMETERLESS_CTOR)
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
    @DisplayName("provide serializable Converter")
    void converter() {
        reserializeAndAssert(converter);
    }
}
