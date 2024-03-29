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

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static io.spine.base.Time.currentTime;
import static io.spine.time.string.TimeStringifiers.forTimestampWebSafe;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("`WebSafeTimestampStringifier` should")
class WebSafeTimestampStringifierTest extends AbstractStringifierTest<Timestamp> {

    WebSafeTimestampStringifierTest() {
        super(forTimestampWebSafe(), Timestamp.class);
    }

    @Override
    protected Timestamp createObject() {
        return currentTime();
    }

    @Test
    @Override
    @DisplayName("be obtained directly via `TimeStringifiers.forTimestampWebSafe()`")
    void isRegistered() {
        // Do not test since this stringifier is not registered.
        // It should be obtained directly via TimeStringifiers.forTimestampWebSafe().
    }

    @Test
    @DisplayName("Throw IllegalArgumentException when parsing unsupported format")
    void parsingError() {
        var webSafeStringifier = stringifier();
        var webSafe = webSafeStringifier.convert(currentTime());
        var corrupt = "XX" + webSafe.substring(2);
        assertThrows(
                IllegalArgumentException.class,
                () -> webSafeStringifier.reverse()
                                        .convert(corrupt)
        );
    }

    @Test
    @DisplayName("replaces colons with dashes")
    void webSafety() {
        var webSafeStringifier = stringifier();
        var webSafe = webSafeStringifier.convert(currentTime());
        var assertOutput = assertThat(webSafe);
        assertOutput.doesNotContain(":");
        assertOutput.contains("-");
    }
}
