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

import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.Immutable;
import com.google.protobuf.Message;
import io.spine.annotation.Internal;
import io.spine.validate.FieldValidatingOption;
import io.spine.validate.ValidatingOptionFactory;

import java.util.Set;

/**
 * An implementation of {@link ValidatingOptionFactory} which adds the {@link When} option for message
 * fields.
 */
@Internal
@Immutable
public final class WhenFactory implements ValidatingOptionFactory {

    private static final ImmutableSet<FieldValidatingOption<?, Message>> TYPE_OPTIONS =
            ImmutableSet.of(When.create());

    @Override
    public Set<FieldValidatingOption<?, Message>> forMessage() {
        return TYPE_OPTIONS;
    }
}