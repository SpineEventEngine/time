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

import io.spine.test.tools.validate.anySpineTemporals
import io.spine.test.tools.validate.futureSpineTemporals
import io.spine.test.tools.validate.pastSpineTemporals
import io.spine.tools.time.validation.java.TemporalFixtures.futureTime
import io.spine.tools.time.validation.java.TemporalFixtures.pastTime
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("If used with repeated `Temporal`, `(when)` constraint should")
internal class TemporalRepeatedWhenSpec {

    @Nested inner class
    `denoting only the past` {

        private val severalPastTimes = listOf(pastTime(), pastTime(), pastTime())

        @Test
        fun `throw, if restricted to be in future`() = assertValidationFails {
            futureSpineTemporals {
                value.addAll(severalPastTimes)
            }
        }

        @Test
        fun `pass, if restricted to be in past`() = assertValidationPasses {
            pastSpineTemporals {
                value.addAll(severalPastTimes)
            }
        }

        @Test
        fun `pass, if not restricted at all`() = assertValidationPasses {
            anySpineTemporals {
                value.addAll(severalPastTimes)
            }
        }
    }

    @Nested inner class
    `denoting only the future` {

        private val severalFutureTimes = listOf(futureTime(), futureTime(), futureTime())

        @Test
        fun `throw, if restricted to be in past`() = assertValidationFails {
            pastSpineTemporals {
                value.addAll(severalFutureTimes)
            }
        }

        @Test
        fun `pass, if restricted to be in future`() = assertValidationPasses {
            futureSpineTemporals {
                value.addAll(severalFutureTimes)
            }
        }

        @Test
        fun `pass, if not restricted at all`() = assertValidationPasses {
            anySpineTemporals {
                value.addAll(severalFutureTimes)
            }
        }
    }

    @Nested inner class
    `with a single past time within the future times` {

        private val severalFutureAndPast = listOf(futureTime(), pastTime(), futureTime())

        @Test
        fun `throw, if restricted to be in future`() = assertValidationFails {
            futureSpineTemporals {
                value.addAll(severalFutureAndPast)
            }
        }

        @Test
        fun `throw, if restricted to be in past`() = assertValidationFails {
            pastSpineTemporals {
                value.addAll(severalFutureAndPast)
            }
        }

        @Test
        fun `pass, if not restricted at all`() = assertValidationPasses {
            anySpineTemporals {
                value.addAll(severalFutureAndPast)
            }
        }
    }

    @Nested inner class
    `with a single future time within the past times` {

        private val severalPastAndFuture = listOf(pastTime(), futureTime(), pastTime())

        @Test
        fun `throw, if restricted to be in future`() = assertValidationFails {
            futureSpineTemporals {
                value.addAll(severalPastAndFuture)
            }
        }

        @Test
        fun `throw, if restricted to be in past`() = assertValidationFails {
            pastSpineTemporals {
                value.addAll(severalPastAndFuture)
            }
        }

        @Test
        fun `pass, if not restricted at all`() = assertValidationPasses {
            anySpineTemporals {
                value.addAll(severalPastAndFuture)
            }
        }
    }
}
