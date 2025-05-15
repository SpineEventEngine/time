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

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
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
import io.spine.validate.TemplateString;
import io.spine.validate.TemplateStrings;
import io.spine.validate.diags.ViolationText;
import io.spine.validate.option.FieldConstraint;

import java.util.Locale;

import static io.spine.time.validation.Time.FUTURE;
import static io.spine.time.validation.Time.TIME_UNDEFINED;

/**
 * A constraint that, when applied to a {@link Timestamp} field value, checks for whether the
 * actual value is in the future or in the past, defined by the value of the field option.
 */
final class WhenConstraint extends FieldConstraint<TimeOption> implements CustomConstraint {

    /**
     * The name of the placeholder for reporting the value of the {@link TimeOption#getIn in}
     * constraint on a temporal field.
     */
    private static final String TIME_PLACEHOLDER = "field.time";

    WhenConstraint(TimeOption optionValue, FieldDeclaration field) {
        super(optionValue, field);
    }

    @Override
    public ImmutableList<ConstraintViolation> validate(MessageValue message) {
        var fieldValue = message.valueOf(field());
        var when = optionValue().getIn();
        if (when == TIME_UNDEFINED) {
            return ImmutableList.of();
        }
        var violations =
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
    public TemplateString errorMessage(FieldContext field) {
        var option = optionValue();
        var errorMsg = ViolationText.errorMessage(option, option.getErrorMsg());
        var time = option.getIn().name().toLowerCase(Locale.ENGLISH);
        var builder = TemplateString.newBuilder()
                .setWithPlaceholders(errorMsg)
                .putPlaceholderValue(TIME_PLACEHOLDER, time);
        TemplateStrings.withField(builder, field.targetDeclaration());
        return builder.build();
    }

    @Override
    public String formattedErrorMessage(FieldContext field) {
        var templateString = errorMessage(field);
        return TemplateStrings.format(templateString);
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
        var valid = (whenExpected == FUTURE)
                    ? temporalValue.isInFuture()
                    : temporalValue.isInPast();
        return !valid;
    }

    private ConstraintViolation newTimeViolation(FieldValue fieldValue, Temporal<?> value) {
        var msg = errorMessage(fieldValue.context());
        var fieldPath = fieldValue.context().fieldPath();
        var violation = ConstraintViolation.newBuilder()
                .setMessage(msg)
                .setFieldPath(fieldPath)
                .setFieldValue(value.toAny())
                .build();
        return violation;
    }
}
