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

import com.google.common.collect.Iterables;
import com.google.protobuf.Message;
import io.spine.validate.FieldValidatingOption;
import io.spine.validate.ValidatingOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

@DisplayName("TimeValidatingOptions should")
class TimeValidatingOptionsTest {

    @Test
    @DisplayName("be discoverable to ServiceLoader")
    void beLoaded() {
        ServiceLoader<ValidatingOptions> loader = ServiceLoader.load(ValidatingOptions.class);
        List<ValidatingOptions> optionFactories = newArrayList(loader);
        assertThat(optionFactories.size()).isAtLeast(1);
        Optional<ValidatingOptions> timeOptionFactory = optionFactories
                .stream()
                .filter(TimeValidatingOptions.class::isInstance)
                .findAny();
        assertThat(timeOptionFactory).isPresent();
    }

    @Test
    @DisplayName("declare (when) option")
    void declareWhen() {
        ValidatingOptions factory = new TimeValidatingOptions();
        Set<FieldValidatingOption<?, Message>> messageOptions = factory.forMessage();
        assertThat(messageOptions).hasSize(1);
        FieldValidatingOption<?, Message> option = Iterables.getOnlyElement(messageOptions);
        assertThat(option).isInstanceOf(When.class);
    }
}
