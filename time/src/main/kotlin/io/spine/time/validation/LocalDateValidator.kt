/*
 * Copyright 2026, TeamDev. All rights reserved.
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

package io.spine.time.validation

import com.google.auto.service.AutoService
import io.spine.base.FieldPath
import io.spine.time.LocalDate
import io.spine.time.Month
import io.spine.validation.DetectedViolation
import io.spine.validation.FieldViolation
import io.spine.validation.MessageValidator
import io.spine.validation.RuntimeErrorPlaceholder.FIELD_PATH
import io.spine.validation.RuntimeErrorPlaceholder.RANGE_VALUE
import io.spine.validation.templateString
import java.time.Year
import java.time.YearMonth

/**
 * Validates [LocalDate] messages.
 *
 * Ensures that the day of a month is within the range allowed for the given month.
 * This takes into account the number of days in February in leap years.
 */
@AutoService(MessageValidator::class)
public class LocalDateValidator : MessageValidator<LocalDate> {

    @Suppress("ReturnCount")
    override fun validate(message: LocalDate): List<DetectedViolation> {
        val year = message.year
        val month = message.month
        val day = message.day

        if (month == Month.MONTH_UNDEFINED || month == Month.UNRECOGNIZED) {
            return emptyList()
        }

        if (year < Year.MIN_VALUE || year > Year.MAX_VALUE) {
            // This is a safety net for the `YearMonth.of()` call which fails
            // when the year is out of range defined by Java Time.
            // We return an empty list because we have an option-based constraint
            // on the `year` field for these values, and validation will fail in the generated code.
            // We do not want to duplicate the error message for the `year` being out of range.
            return emptyList()
        }

        val daysInMonth = YearMonth.of(year, month.number).lengthOfMonth()
        if (day > daysInMonth) {
            return listOf(invalidDay(day, daysInMonth))
        }
        return emptyList()
    }
}

/**
 * Creates a violation for an invalid day of the month.
 *
 * @param day the invalid day value.
 * @param maxDays the maximum allowed days for the month.
 */
private fun invalidDay(day: Int, maxDays: Int): FieldViolation = FieldViolation(
    message = templateString {
        withPlaceholders = "The \${$FIELD_PATH} value is out of range" +
                " (\${$RANGE_VALUE}): $day."
        placeholderValue.put(FIELD_PATH.value, "day")
        placeholderValue.put(RANGE_VALUE.value, "1..$maxDays")
    },
    fieldPath = FieldPath.newBuilder()
        .addFieldName("day")
        .build(),
    fieldValue = day
)
