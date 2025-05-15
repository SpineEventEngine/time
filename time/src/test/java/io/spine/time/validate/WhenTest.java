/*
 * Copyright 2025, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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

package io.spine.time.validate;

import io.spine.base.Time;
import io.spine.validate.ConstraintViolation;
import io.spine.validate.TemplateString;
import io.spine.validate.TemplateStrings;
import io.spine.validate.ValidationError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static io.spine.string.Strings.count;
import static io.spine.time.validate.given.WhenTestEnv.FIFTY_NANOSECONDS;
import static io.spine.time.validate.given.WhenTestEnv.ZERO_NANOSECONDS;
import static io.spine.time.validate.given.WhenTestEnv.currentTimeWithNanos;
import static io.spine.time.validate.given.WhenTestEnv.freezeTime;
import static io.spine.time.validate.given.WhenTestEnv.future;
import static io.spine.time.validate.given.WhenTestEnv.past;
import static io.spine.time.validate.given.WhenTestEnv.timeWithNanos;

@DisplayName("`(when)` option should")
class WhenTest {

    @AfterEach
    void tearDown() {
        Time.resetProvider();
    }

    @Test
    @DisplayName("find out that time is in future")
    void findOutThatTimeIsInFuture() {
        var validMsg = TimeInFutureFieldValue.newBuilder()
                .setValue(future())
                .build();
        assertThat(validMsg.validate())
                .isEmpty();
    }

    @Test
    @DisplayName("find out that time is NOT in future")
    void findOutThatTimeIsNotInFuture() {
        var invalidMsg = TimeInFutureFieldValue.newBuilder()
                .setValue(past())
                .buildPartial();
        var error = invalidMsg.validate();
        assertThat(error)
                .isPresent();
        assertViolations(error.get());
    }

    @Test
    @DisplayName("find out that time is in past")
    void findOutThatTimeIsInPast() {
        var validMsg = TimeInPastFieldValue.newBuilder()
                .setValue(past())
                .build();
        assertThat(validMsg.validate())
                .isEmpty();
    }

    @Test
    @DisplayName("find out that time is NOT in past")
    void findOutThatTimeIsNotInPast() {
        var invalidMsg = TimeInPastFieldValue.newBuilder()
                .setValue(future())
                .buildPartial();
        var error = invalidMsg.validate();
        assertThat(error)
                .isPresent();
        assertViolations(error.get());
    }

    @Test
    @DisplayName("find out that time is NOT in the past by nanoseconds")
    void findOutThatTimeIsNotInThePastByNanos() {
        var currentTime = currentTimeWithNanos(ZERO_NANOSECONDS);
        var timeInFuture = timeWithNanos(currentTime, FIFTY_NANOSECONDS);
        freezeTime(currentTime);
        var invalidMsg = TimeInPastFieldValue.newBuilder()
                .setValue(timeInFuture)
                .buildPartial();
        var error = invalidMsg.validate();
        assertThat(error)
                .isPresent();
        assertViolations(error.get());
    }

    @Test
    @DisplayName("find out that time is in the past by nanoseconds")
    void findOutThatTimeIsInThePastByNanos() {
        var currentTime = currentTimeWithNanos(FIFTY_NANOSECONDS);
        var timeInPast = timeWithNanos(currentTime, ZERO_NANOSECONDS);
        freezeTime(currentTime);
        var validMsg = TimeInPastFieldValue.newBuilder()
                .setValue(timeInPast)
                .build();
        assertThat(validMsg.validate())
                .isEmpty();
    }

    @Test
    @DisplayName("consider `Timestamp` field valid if no `TimeOption` set")
    void considerTimestampFieldIsValidIfNoTimeOptionSet() {
        var msg = TimeWithoutOptsFieldValue.getDefaultInstance();
        assertThat(msg.validate())
                .isEmpty();
    }

    @Test
    @DisplayName("provide one valid violation if time is invalid")
    void provideOneValidViolationIfTimeIsInvalid() {
        var invalidMsg = TimeInFutureFieldValue.newBuilder()
                .setValue(past())
                .buildPartial();
        var optionalError = invalidMsg.validate();
        assertThat(optionalError)
                .isPresent();
        var error = optionalError.get();
        assertSingleViolation(error, "The time must be in the future.");
    }

    private static void assertSingleViolation(ValidationError error, String expectedMsg) {
        var violations = error.getConstraintViolationList();
        assertThat(violations)
                .hasSize(1);
        var violation = violations.get(0);
        var actualErrorMessage = TemplateStrings.format(violation.getMessage());
        assertThat(actualErrorMessage).isEqualTo(expectedMsg);
    }

    @Test
    @DisplayName("ignore `(when).in = TIME_UNDEFINED` if in the past")
    void ignoreTimeUndefinedInFuture() {
        var validTimeInPast = AlwaysValidTime.newBuilder()
                .setValue(past())
                .build();
        assertThat(validTimeInPast.validate())
                .isEmpty();
    }

    @Test
    @DisplayName("ignore `(when).in = TIME_UNDEFINED` if in the future")
    void ignoreTimeUndefinedInPast() {
        var validTimeInPast = AlwaysValidTime.newBuilder()
                .setValue(future())
                .build();
        assertThat(validTimeInPast.validate())
                .isEmpty();
    }

    private static void assertViolations(ValidationError error) {
        var violations = error.getConstraintViolationList();
        assertThat(violations)
                .isNotEmpty();
    }

    @Test
    @DisplayName("provide correct format of the violation message")
    void provideCorrectFormatOfViolationMessage() {
        var invalidMsg = TimeWithDefaultErrorMessage.newBuilder()
                .setValue(past())
                .buildPartial();
        var optionalError = invalidMsg.validate();
        assertHasCorrectFormat(optionalError.get().getConstraintViolationList().get(0));
    }

    private static void assertHasCorrectFormat(ConstraintViolation violation) {
        var message = violation.getMessage();
        assertThat(message).isNotEqualTo(TemplateString.getDefaultInstance());
        var errorMessage = message.getWithPlaceholders();
        var numberOfPlaceholders = count(errorMessage, "${");
        if (numberOfPlaceholders != 0) {
            assertThat(message.getPlaceholderValueMap()).hasSize(numberOfPlaceholders);
        } else {
            assertThat(message.getPlaceholderValueMap()).isEmpty();
        }
    }
}
