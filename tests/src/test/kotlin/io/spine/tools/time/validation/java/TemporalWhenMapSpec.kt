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

package io.spine.tools.time.validation.java

import io.spine.test.tools.validate.anySpineTemporalMap
import io.spine.test.tools.validate.futureSpineTemporalMap
import io.spine.test.tools.validate.pastSpineTemporalMap
import io.spine.tools.time.validation.java.TemporalFixtures.futureTime
import io.spine.tools.time.validation.java.TemporalFixtures.pastTime
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("If used with a map of `Temporal` values, `(when)` constraint should")
internal class TemporalWhenMapSpec {

    @Nested inner class
    `only the past` {

        private val severalPastTimes = mapOf("a" to pastTime(), "b" to pastTime())

        @Test
        fun `throw, if restricted to be in future`() = assertValidationFails {
            futureSpineTemporalMap {
                value.putAll(severalPastTimes)
            }
        }

        @Test
        fun `pass, if restricted to be in past`() = assertValidationPasses {
            pastSpineTemporalMap {
                value.putAll(severalPastTimes)
            }
        }

        @Test
        fun `pass, if not restricted at all`() = assertValidationPasses {
            anySpineTemporalMap {
                value.putAll(severalPastTimes)
            }
        }
    }

    @Nested inner class
    `only the future` {

        private val severalFutureTimes = mapOf("a" to futureTime(), "b" to futureTime())

        @Test
        fun `throw, if restricted to be in past`() = assertValidationFails {
            pastSpineTemporalMap {
                value.putAll(severalFutureTimes)
            }
        }

        @Test
        fun `pass, if restricted to be in future`() = assertValidationPasses {
            futureSpineTemporalMap {
                value.putAll(severalFutureTimes)
            }
        }

        @Test
        fun `pass, if not restricted at all`() = assertValidationPasses {
            anySpineTemporalMap {
                value.putAll(severalFutureTimes)
            }
        }
    }

    @Nested inner class
    `a mix of past and future` {

        private val mixedTimes = mapOf("a" to futureTime(), "b" to pastTime())

        @Test
        fun `throw, if restricted to be in future`() = assertValidationFails {
            futureSpineTemporalMap {
                value.putAll(mixedTimes)
            }
        }

        @Test
        fun `throw, if restricted to be in past`() = assertValidationFails {
            pastSpineTemporalMap {
                value.putAll(mixedTimes)
            }
        }

        @Test
        fun `pass, if not restricted at all`() = assertValidationPasses {
            anySpineTemporalMap {
                value.putAll(mixedTimes)
            }
        }
    }
}
