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

import io.spine.time.LocalDate;
import io.spine.time.LocalDates;

/**
 * The default stringifier for {@link io.spine.time.LocalDate LocalDate} instances.
 *
 * @author Alexander Yevsyukov
 */
final class LocalDateStringifier extends JtStringifier<LocalDate, java.time.LocalDate> {

    private static final long serialVersionUID = 0L;
    private static final LocalDateStringifier INSTANCE = new LocalDateStringifier();

    /** Prevents instantiation from outside. */
    private LocalDateStringifier() {
        super(LocalDates.converter());
    }

    static LocalDateStringifier getInstance() {
        return INSTANCE;
    }

    @Override
    java.time.LocalDate parse(String str) {
        return java.time.LocalDate.parse(str);
    }

    @Override
    public String toString() {
        return "TimeStringifiers.forLocalDate()";
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
