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

@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.spine.time.kotlin

import com.google.protobuf.Duration as ProtoDuration
import com.google.protobuf.Timestamp
import com.google.protobuf.timestamp
import kotlin.time.Instant as KtInstant
import kotlin.time.Duration as KtDuration
import kotlin.time.Duration.Companion.nanoseconds

/**
 * Converts this `Timestamp` to KotlinX `Instant`.
 */
public fun Timestamp.toInstant(): KtInstant =
    KtInstant.fromEpochSeconds(this.seconds, this.nanos.toLong())

/**
 * Converts this KotlinX `Instant` to Protobuf `Timestamp`.
 */
public fun KtInstant.toTimestamp(): Timestamp = timestamp {
    seconds = this@toTimestamp.epochSeconds
    nanos = this@toTimestamp.nanosecondsOfSecond
}

private const val NANOS_IN_SECOND = 1_000_000_000L

/**
 * Converts this Protobuf `Duration` to Kotlin `kotlin.time.Duration`.
 */
public fun ProtoDuration.toKotlinDuration(): KtDuration {
    val totalNanos = this.seconds * NANOS_IN_SECOND + this.nanos
    return totalNanos.nanoseconds
}

/**
 * Converts this Kotlin `kotlin.time.Duration` to Protobuf `Duration`.
 */
public fun KtDuration.toProtoDuration(): ProtoDuration {
    val totalNanos = this.inWholeNanoseconds
    val seconds = totalNanos / NANOS_IN_SECOND // truncates toward zero
    val nanos = (totalNanos % NANOS_IN_SECOND).toInt()
    return ProtoDuration.newBuilder()
        .setSeconds(seconds)
        .setNanos(nanos)
        .build()
}
