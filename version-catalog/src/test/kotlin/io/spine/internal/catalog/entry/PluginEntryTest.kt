/*
 * Copyright 2022, TeamDev. All rights reserved.
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

package io.spine.internal.catalog.entry

import io.spine.internal.catalog.entry.given.OuterDummyPlugin
import io.spine.internal.catalog.entry.given.PluginEntryTestEnv.Companion.assert
import io.spine.internal.catalog.entry.given.PluginEntryTestEnv.Companion.record
import io.spine.internal.catalog.entry.given.StandaloneDummyPlugin
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("`PluginEntry` should when")
internal class PluginEntryTest {

    @Nested
    inner class standalone {

        @Test
        fun `assemble a plugin record if the id and version are specified`(){
            val record = record(StandaloneDummyPlugin)
            record.assert(alias = "standaloneDummyPlugin", id = "dummy-plugin")
        }
    }

    @Nested
    inner class nested {

        @Test
        fun `not prepend a 'GradlePlugin' prefix for the plugin id `() {
            val record = record(OuterDummyPlugin.GradlePlugin)
            record.assert(alias = "outerDummyPlugin", id = "dummy-gradle-plugin")
        }
    }
}