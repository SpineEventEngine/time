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

package io.spine.time.string;

import io.spine.string.Stringifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.testing.SerializableTester.reserializeAndAssert;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The abstract base for stringifier tests.
 *
 * @author Alexander Yevsyukov
 * @param <T> the type of stringifier objects
 */
abstract class AbstractStringifierTest<T> {

    private final Stringifier<T> stringifier;

    AbstractStringifierTest(Stringifier<T> stringifier) {
        this.stringifier = stringifier;
    }

    protected abstract T createObject();

    Stringifier<T> getStringifier() {
        return stringifier;
    }

    @Test
    @DisplayName("Convert forward and backward")
    void convert() {
        T obj = createObject();

        final String str = stringifier.convert(obj);
        final T convertedBack = stringifier.reverse()
                                           .convert(str);

        assertEquals(obj, convertedBack);
    }

    @Test
    @DisplayName("Prohibit empty string input")
    void prohibitEmptyString() {
        assertThrows(
                IllegalArgumentException.class,
                () -> stringifier.reverse()
                                 .convert("")
        );
    }

    @Test
    @DisplayName("Serialize")
    void serialize() {
        Stringifier<T> expected = getStringifier();
        Stringifier<T> stringifier = reserializeAndAssert(expected);
        assertSame(expected, stringifier);
    }
}
