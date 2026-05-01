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

import io.spine.test.tools.validate.anyProtoTimestamps
import io.spine.test.tools.validate.futureProtoTimestamps
import io.spine.test.tools.validate.pastProtoTimestamps
import io.spine.tools.time.validation.java.TimestampFixtures.futureTime
import io.spine.tools.time.validation.java.TimestampFixtures.pastTime
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("If used with repeated Protobuf `Timestamp`, `(when)` constraint should")
internal class TimestampRepeatedWhenSpec {

    @Nested inner class
    `when given several timestamps` {

        @Nested inner class
        `denoting only the past` {

            private val severalPastTimes = listOf(pastTime(), pastTime(), pastTime())

            @Test
            fun `throw, if restricted to be in future`() = assertValidationFails {
                futureProtoTimestamps {
                    value.addAll(severalPastTimes)
                }
            }

            @Test
            fun `pass, if restricted to be in past`() = assertValidationPasses {
                pastProtoTimestamps {
                    value.addAll(severalPastTimes)
                }
            }

            @Test
            fun `pass, if not restricted at all`() = assertValidationPasses {
                anyProtoTimestamps {
                    value.addAll(severalPastTimes)
                }
            }
        }

        @Nested inner class
        `denoting only the future` {

            private val severalFutureTimes = listOf(futureTime(), futureTime(), futureTime())

            @Test
            fun `throw, if restricted to be in past`() = assertValidationFails {
                pastProtoTimestamps {
                    value.addAll(severalFutureTimes)
                }
            }

            @Test
            fun `pass, if restricted to be in future`() = assertValidationPasses {
                futureProtoTimestamps {
                    value.addAll(severalFutureTimes)
                }
            }

            @Test
            fun `pass, if not restricted at all`() = assertValidationPasses {
                anyProtoTimestamps {
                    value.addAll(severalFutureTimes)
                }
            }
        }

        @Nested inner class
        `with a single past stamp within the future stamps` {

            private val severalFutureAndPast = listOf(futureTime(), pastTime(), futureTime())

            @Test
            fun `throw, if restricted to be in future`() = assertValidationFails {
                futureProtoTimestamps {
                    value.addAll(severalFutureAndPast)
                }
            }

            @Test
            fun `throw, if restricted to be in past`() = assertValidationFails {
                pastProtoTimestamps {
                    value.addAll(severalFutureAndPast)
                }
            }

            @Test
            fun `pass, if not restricted at all`() = assertValidationPasses {
                anyProtoTimestamps {
                    value.addAll(severalFutureAndPast)
                }
            }
        }

        @Nested inner class
        `with a single future stamp within the past stamps` {

            private val severalPastAndFuture = listOf(pastTime(), futureTime(), pastTime())

            @Test
            fun `throw, if restricted to be in future`() = assertValidationFails {
                futureProtoTimestamps {
                    value.addAll(severalPastAndFuture)
                }
            }

            @Test
            fun `throw, if restricted to be in past`() = assertValidationFails {
                pastProtoTimestamps {
                    value.addAll(severalPastAndFuture)
                }
            }

            @Test
            fun `pass, if not restricted at all`() = assertValidationPasses {
                anyProtoTimestamps {
                    value.addAll(severalPastAndFuture)
                }
            }
        }
    }
}
