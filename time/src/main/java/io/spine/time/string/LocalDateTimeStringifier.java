/*
 * Copyright 2018, TeamDev. All rights reserved.
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

package io.spine.time.string;

import io.spine.time.LocalDateTime;
import io.spine.time.LocalDateTimes;

/**
 * The default stringifier for {@link io.spine.time.LocalDateTime LocalDateTime} values.
 */
final class LocalDateTimeStringifier extends JtStringifier<LocalDateTime, java.time.LocalDateTime> {

    private static final long serialVersionUID = 0L;
    private static final LocalDateTimeStringifier INSTANCE = new LocalDateTimeStringifier();

    /** Prevents instantiation from outside. */
    private LocalDateTimeStringifier() {
        super(LocalDateTimes.converter());
    }

    static LocalDateTimeStringifier getInstance() {
        return INSTANCE;
    }

    @Override
    java.time.LocalDateTime parse(String str) {
        return java.time.LocalDateTime.parse(str);
    }

    @Override
    public String toString() {
        return "TimeStringifiers.forLocalDateTime()";
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
