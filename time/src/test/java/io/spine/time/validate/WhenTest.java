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

package io.spine.time.validate;

import com.google.protobuf.Duration;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import io.spine.base.Time;
import io.spine.validate.ConstraintViolation;
import io.spine.validate.MessageValidator;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.time.validate.given.WhenTestEnv.FIFTY_NANOSECONDS;
import static io.spine.time.validate.given.WhenTestEnv.ZERO_NANOSECONDS;
import static io.spine.time.validate.given.WhenTestEnv.currentTimeWithNanos;
import static io.spine.time.validate.given.WhenTestEnv.freezeTime;
import static io.spine.time.validate.given.WhenTestEnv.future;
import static io.spine.time.validate.given.WhenTestEnv.past;
import static io.spine.time.validate.given.WhenTestEnv.timeWithNanos;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("(when) option should")
class WhenTest {

    private @Nullable List<ConstraintViolation> violations;

    @AfterEach
    void tearDown() {
        Time.resetProvider();
        violations = null;
    }

    @Test
    @DisplayName("find out that time is in future")
    void findOutThatTimeIsInFuture() {
        TimeInFutureFieldValue validMsg = TimeInFutureFieldValue
                .newBuilder()
                .setValue(future())
                .build();
        assertValid(validMsg);
    }

    @Test
    @DisplayName("find out that time is NOT in future")
    void findOutThatTimeIsNotInFuture() {
        TimeInFutureFieldValue invalidMsg = TimeInFutureFieldValue
                .newBuilder()
                .setValue(past())
                .build();
        assertNotValid(invalidMsg);
    }

    @Test
    @DisplayName("find out that time is in past")
    void findOutThatTimeIsInPast() {
        TimeInPastFieldValue validMsg = TimeInPastFieldValue
                .newBuilder()
                .setValue(past())
                .build();
        assertValid(validMsg);
    }

    @Test
    @DisplayName("find out that time is NOT in past")
    void findOutThatTimeIsNotInPast() {
        TimeInPastFieldValue invalidMsg = TimeInPastFieldValue
                .newBuilder()
                .setValue(future())
                .build();
        assertNotValid(invalidMsg);
    }

    @Test
    @DisplayName("find out that time is NOT in the past by nanoseconds")
    void findOutThatTimeIsNotInThePastByNanos() {
        Timestamp currentTime = currentTimeWithNanos(ZERO_NANOSECONDS);
        Timestamp timeInFuture = timeWithNanos(currentTime, FIFTY_NANOSECONDS);
        freezeTime(currentTime);
        TimeInPastFieldValue invalidMsg =
                TimeInPastFieldValue.newBuilder()
                                    .setValue(timeInFuture)
                                    .build();
        assertNotValid(invalidMsg);
    }

    @Test
    @DisplayName("find out that time is in the past by nanoseconds")
    void findOutThatTimeIsInThePastByNanos() {
        Timestamp currentTime = currentTimeWithNanos(FIFTY_NANOSECONDS);
        Timestamp timeInPast = timeWithNanos(currentTime, ZERO_NANOSECONDS);
        freezeTime(currentTime);
        TimeInPastFieldValue invalidMsg =
                TimeInPastFieldValue.newBuilder()
                                    .setValue(timeInPast)
                                    .build();
        assertValid(invalidMsg);
    }

    @Test
    @DisplayName("consider Timestamp field valid if no TimeOption set")
    void considerTimestampFieldIsValidIfNoTimeOptionSet() {
        TimeWithoutOptsFieldValue msg = TimeWithoutOptsFieldValue.getDefaultInstance();
        assertValid(msg);
    }

    @Test
    @DisplayName("provide one valid violation if time is invalid")
    void provideOneValidViolationIfTimeIsInvalid() {
        TimeInFutureFieldValue invalidMsg = TimeInFutureFieldValue
                .newBuilder()
                .setValue(past())
                .build();
        assertSingleViolation(invalidMsg, "Point in time must be in the future.");
    }

    @Test
    @DisplayName("ignore (when).in = TIME_UNDEFINED if in the past")
    void ignoreTimeUndefinedInFuture() {
        AlwaysValidTime validTimeInPast = AlwaysValidTime
                .newBuilder()
                .setValue(past())
                .build();
        assertValid(validTimeInPast);
    }

    @Test
    @DisplayName("ignore (when).in = TIME_UNDEFINED if in the future")
    void ignoreTimeUndefinedInPast() {
        AlwaysValidTime validTimeInPast = AlwaysValidTime
                .newBuilder()
                .setValue(future())
                .build();
        assertValid(validTimeInPast);
    }

    @Test
    @DisplayName("throw an IllegalArgumentException if applied on a non-temporal type")
    void throwIaeOnWrongType() {
        WhenMisuse failingMessage = WhenMisuse
                .newBuilder()
                .setDuration(Duration.getDefaultInstance())
                .build();
        assertThrows(IllegalArgumentException.class, () -> validate(failingMessage));
    }

    private void validate(Message msg) {
        violations = MessageValidator.validate(msg);
    }

    private ConstraintViolation firstViolation() {
        return violations.get(0);
    }

    private void assertValid(Message msg) {
        validate(msg);
        assertIsValid(true);
    }

    private void assertNotValid(Message msg) {
        validate(msg);
        assertIsValid(false);
    }

    private void assertIsValid(boolean isValid) {
        assertNotNull(violations);
        if (isValid) {
            assertTrue(violations.isEmpty(), () -> violations.toString());
        } else {
            assertViolations();
        }
    }

    private void assertViolations() {
        assertNotNull(violations);
        assertFalse(violations.isEmpty());
        for (ConstraintViolation violation : violations) {
            assertHasCorrectFormat(violation);
        }
    }

    private static void assertHasCorrectFormat(ConstraintViolation violation) {
        String format = violation.getMsgFormat();
        assertFalse(format.isEmpty());
        boolean noParams = violation.getParamList()
                                    .isEmpty();
        if (format.contains("%s")) {
            assertFalse(noParams);
        } else {
            assertTrue(noParams);
        }
    }

    private void assertSingleViolation(Message message, String expectedErrMsg) {
        assertNotValid(message);
        assertNotNull(violations);
        assertEquals(1, violations.size());
        assertSingleViolation(expectedErrMsg);
    }

    /** Checks that a message is not valid and has a single violation. */
    private void assertSingleViolation(String expectedErrMsg) {
        ConstraintViolation violation = firstViolation();
        String actualErrorMessage = format(violation.getMsgFormat(), violation.getParamList()
                                                                              .toArray());
        assertEquals(expectedErrMsg, actualErrorMessage);
        assertTrue(violation.getViolationList()
                            .isEmpty());
    }
}
