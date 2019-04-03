/*
 * Copyright 2019, TeamDev. All rights reserved.
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

package io.spine.time.validate;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import io.spine.time.validation.TimeOption;
import io.spine.time.validation.TimeOptionsProto;
import io.spine.validate.Constraint;
import io.spine.validate.FieldValidatingOption;
import io.spine.validate.FieldValue;

/**
 * A validating option that specified the point in time which a {@link Timestamp} field value
 * has.
 *
 * @param <T>
 *         the type of validated message
 */
final class When extends FieldValidatingOption<TimeOption, Message> {

    private When() {
        super(TimeOptionsProto.when);
    }

    /** Creates a new instance of this option. */
    public static When create() {
        return new When();
    }

    @Override
    public Constraint<FieldValue<Message>> constraintFor(FieldValue<Message> value) {
        return new WhenConstraint(optionValue(value));
    }
}
