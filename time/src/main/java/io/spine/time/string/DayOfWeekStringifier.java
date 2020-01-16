/*
 * Copyright 2020, TeamDev. All rights reserved.
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

import io.spine.string.SerializableStringifier;
import io.spine.string.Stringifier;
import io.spine.time.DayOfWeek;

import static io.spine.string.Stringifiers.newForEnum;

/**
 * The default stringifier for {@link io.spine.time.DayOfWeek DayOfWeek} instances.
 */
final class DayOfWeekStringifier extends SerializableStringifier<DayOfWeek> {

    private static final long serialVersionUID = 0L;
    private static final DayOfWeekStringifier INSTANCE = new DayOfWeekStringifier();

    private final Stringifier<DayOfWeek> delegate = newForEnum(DayOfWeek.class);

    private DayOfWeekStringifier() {
        super("TimeStringifiers.forDayOfWeek()");
    }

    @Override
    protected String toString(DayOfWeek dayOfWeek) {
        return delegate.convert(dayOfWeek);
    }

    @Override
    protected DayOfWeek fromString(String s) {
        return delegate.reverse()
                       .convert(s);
    }

    static DayOfWeekStringifier instance() {
        return INSTANCE;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
