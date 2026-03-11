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

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.spine.time.LocalDate
import io.spine.time.Month
import io.spine.time.localDate
import io.spine.validation.FieldViolation
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("`LocalDateValidator` should")
internal class LocalDateValidatorSpec {

    private val validator = LocalDateValidator()

    @Test
    fun `allow valid dates`() {
        assertDoesNotThrow {
            localDate {
                year = 2026
                month = Month.JANUARY
                day = 31
            }
        }
    }

    @ParameterizedTest
    @DisplayName("detect invalid day for a month")
    @CsvSource(
        "2024, APRIL, 31, 30",
        "2024, JUNE, 31, 30",
        "2024, SEPTEMBER, 31, 30",
        "2024, NOVEMBER, 31, 30",
        "2023, FEBRUARY, 29, 28",
        "2024, FEBRUARY, 30, 29"
    )
    fun detectInvalidDay(year: Int, month: Month, day: Int, maxDays: Int) {
        val date = LocalDate.newBuilder()
            .setYear(year)
            .setMonth(month)
            .setDay(day)
            .buildPartial()
        val violations = validator.validate(date)
        violations shouldHaveSize 1
        val violation = violations[0] as FieldViolation
        violation.run {
            fieldPath!!.fieldNameList[0] shouldBe "day"
            fieldValue shouldBe day
            message.withPlaceholders shouldBe
                    "The \${field.path} value is out of range (\${range.value}): $day."
            message.placeholderValueMap["range.value"] shouldBe "1..$maxDays"
        }
    }

    @Nested
    @DisplayName("handle leap years and")
    inner class LeapYear {

        @Test
        fun `allow Feb 29 in a leap year`() {
            val date = LocalDate.newBuilder()
                .setYear(2024)
                .setMonth(Month.FEBRUARY)
                .setDay(29)
                .buildPartial()
            validator.validate(date).shouldBeEmpty()
        }

        @Test
        fun `detect invalid Feb 29 in a non-leap year`() {
            val date = LocalDate.newBuilder()
                .setYear(2023)
                .setMonth(Month.FEBRUARY)
                .setDay(29)
                .buildPartial()
            val violations = validator.validate(date)
            violations shouldHaveSize 1
            val violation = violations[0] as FieldViolation
            violation.message.placeholderValueMap["range.value"] shouldBe "1..28"
        }
    }

    /**
     * The test verifies that if a month is not defined the `LocalDate` instance
     * is considered valid.
     *
     * There is nothing we can do in such a situation because `Month` is an enum.
     * We do not restrict enum field values because it does not have much sense
     * from the domain language point of view.
     *
     * We still want `MONTH_UNDEFINED` item to support the "unset" notion for a month
     * as we have such a thing for other enums in the code.
     */
    @Test
    fun `ignore undefined month`() {
        val date = LocalDate.newBuilder()
            .setYear(2024)
            .setMonth(Month.MONTH_UNDEFINED)
            .setDay(31)
            .buildPartial()
        validator.validate(date).shouldBeEmpty()
    }
}
