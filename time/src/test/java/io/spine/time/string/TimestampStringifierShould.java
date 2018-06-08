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

import com.google.protobuf.Timestamp;
import io.spine.string.Stringifiers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static io.spine.base.Time.getCurrentTime;

/**
 * @author Alexander Yevsyukov
 */
public class TimestampStringifierShould extends AbstractStringifierTest<Timestamp> {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public TimestampStringifierShould() {
        super(TimeStringifiers.forTimestamp());
    }

    @Override
    protected Timestamp createObject() {
        return getCurrentTime();
    }

    @Test
    public void throw_exception_when_try_to_convert_inappropriate_string_to_timestamp() {
        // This uses TextFormat printing, for the output won't be parsable.
        String time = getCurrentTime().toString();
        thrown.expect(IllegalArgumentException.class);
        Stringifiers.fromString(time, Timestamp.class);
    }
}
