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

import io.spine.internal.catalog.CatalogRecord
import io.spine.internal.catalog.LibraryNotation
import io.spine.internal.catalog.LibraryRecord
import io.spine.internal.catalog.VersionRecord

/**
 * A catalog entry, which is used to declare a library.
 *
 * Only object declarations are meant to inherit from this class.
 *
 * Below is an example of how to declare a library using this entry:
 *
 * ```
 * internal object MyLib : LibraryEntry() {
 *     override val version = "1.0.0"
 *     override val module = "org.my.company:my-lib"
 * }
 * ```
 */
abstract class LibraryEntry : CatalogEntry(), LibraryNotation {

    /**
     * Produces [VersionRecord] and [LibraryRecord].
     */
    override fun records(): Set<CatalogRecord> {
        val version = VersionRecord(alias, version)
        val library = LibraryRecord(alias, module, alias)
        return setOf(version, library)
    }
}