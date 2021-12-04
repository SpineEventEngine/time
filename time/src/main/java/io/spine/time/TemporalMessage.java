/*
 * Copyright 2021, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

package io.spine.time;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import io.spine.annotation.Internal;
import io.spine.protobuf.AnyPacker;

/**
 * A {@link Temporal} implemented with a message.
 *
 * <p>Messages marked with the {@code Temporal} interface should use this type instead of using
 * the {@code Temporal} directly.
 *
 * <p>To create a temporal message type:
 * <ol>
 *     <li>create a new interface derived from this one;
 *     <li>specify the target message type as the type parameter;
 *     <li>implement leftover abstract methods inherited from {@link Temporal};
 *     <li>mark the target message with the {@code (is)} option.
 * </ol>
 *
 * <p>Interfaces derived from this one are meant to be implemented only by corresponding
 * message type defined by the generic parameter {@code <T>}. As such, they should not add any
 * abstract methods because it would break the generated code.
 *
 * @param <T>
 *         the type of itself
 */
@Internal
public interface TemporalMessage<T extends TemporalMessage<T>> extends Temporal<T>, Message {

    /**
     * Packs this message into an {@code Any}.
     *
     * @return this message as an {@code Any}
     */
    @Override
    default Any toAny() {
        var any = AnyPacker.pack(this);
        return any;
    }
}
