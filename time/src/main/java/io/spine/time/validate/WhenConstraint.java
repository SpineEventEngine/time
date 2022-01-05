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

package io.spine.time.validate;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import io.spine.base.FieldPath;
import io.spine.code.proto.FieldContext;
import io.spine.code.proto.FieldDeclaration;
import io.spine.time.Temporal;
import io.spine.time.Temporals;
import io.spine.time.validation.Time;
import io.spine.time.validation.TimeOption;
import io.spine.validate.ConstraintViolation;
import io.spine.validate.CustomConstraint;
import io.spine.validate.FieldValue;
import io.spine.validate.MessageValue;
import io.spine.validate.diags.ViolationText;
import io.spine.validate.option.FieldConstraint;

import static io.spine.time.validation.Time.FUTURE;
import static io.spine.time.validation.Time.TIME_UNDEFINED;

/**
 * A constraint that, when applied to a {@link Timestamp} field value, checks for whether the
 * actual value is in the future or in the past, defined by the value of the field option.
 */
final class WhenConstraint extends FieldConstraint<TimeOption> implements CustomConstraint {

    WhenConstraint(TimeOption optionValue, FieldDeclaration field) {
        super(optionValue, field);
    }

    @Override
    public ImmutableList<ConstraintViolation> validate(MessageValue message) {
        FieldValue fieldValue = message.valueOf(field());
        Time when = optionValue().getIn();
        if (when == TIME_UNDEFINED) {
            return ImmutableList.of();
        }
        ImmutableList<ConstraintViolation> violations =
                fieldValue.values()
                          .map(msg -> Temporals.from((Message) msg))
                          .filter(temporalValue -> isTimeInvalid(temporalValue, when))
                          .findFirst()
                          .map(invalidValue -> ImmutableList.of(
                                  newTimeViolation(fieldValue, invalidValue)
                          ))
                          .orElse(ImmutableList.of());
        return violations;
    }

    @Override
    @SuppressWarnings("deprecation") /* Old validation won't migrate to the new error messages. */
    public String errorMessage(FieldContext context) {
        return ViolationText.errorMessage(optionValue(), optionValue().getMsgFormat());
    }

    /**
     * Checks the time.
     *
     * @param temporalValue
     *         a time point to check
     * @param whenExpected
     *         the time when the checked timestamp should be
     * @return {@code true} if the time is valid according to {@code whenExpected} parameter,
     *         {@code false} otherwise
     */
    private static boolean isTimeInvalid(Temporal<?> temporalValue, Time whenExpected) {
        boolean valid = (whenExpected == FUTURE)
                          ? temporalValue.isInFuture()
                          : temporalValue.isInPast();
        return !valid;
    }

    private ConstraintViolation newTimeViolation(FieldValue fieldValue, Temporal<?> value) {
        String msg = errorMessage(fieldValue.context());
        String when =
                optionValue().getIn()
                             .toString()
                             .toLowerCase();
        FieldPath fieldPath =
                fieldValue.context()
                          .fieldPath();
        ConstraintViolation violation = ConstraintViolation.newBuilder()
                .setMsgFormat(msg)
                .addParam(when)
                .setFieldPath(fieldPath)
                .setFieldValue(value.toAny())
                .build();
        return violation;
    }
}
