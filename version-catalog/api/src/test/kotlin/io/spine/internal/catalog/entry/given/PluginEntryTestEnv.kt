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

package io.spine.internal.catalog.entry.given

import io.spine.internal.catalog.Alias
import io.spine.internal.catalog.LibraryRecord
import io.spine.internal.catalog.entry.PluginEntry
import io.spine.internal.catalog.PluginRecord
import io.spine.internal.catalog.VersionRecord
import org.junit.jupiter.api.Assertions

internal class PluginEntryTestEnv {
    companion object {

        fun versionRecord(entry: PluginEntry) =
            entry.allRecords().first { it is VersionRecord } as VersionRecord

        fun libraryRecord(entry: PluginEntry) =
            entry.allRecords().first { it is LibraryRecord } as LibraryRecord

        fun pluginRecord(entry: PluginEntry) =
            entry.allRecords().first { it is PluginRecord } as PluginRecord

        fun PluginRecord.assert(alias: String, id: String, versionRef: Alias) {
            Assertions.assertEquals(alias, this.alias)
            Assertions.assertEquals(id, this.id)
            Assertions.assertEquals(versionRef, this.versionRef)
        }
    }
}