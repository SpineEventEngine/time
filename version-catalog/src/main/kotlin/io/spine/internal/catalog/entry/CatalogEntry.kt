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

/**
 * An entry, which produces no records.
 *
 * It can be used as an outer entry to introduce a common alias. Such entries
 * don't declare anything on their own, they just serve as a named scope for
 * nested declarations.
 *
 * Please, consider the following example:
 *
 * ```
 * internal object Runtime : CatalogEntry() {
 *     object Linux : SomeEntry()  // alias = runtime.linux
 *     object Mac : SomeEntry()    // alias = runtime.mac
 *     object Win : SomeEntry()    // alias = runtime.win
 * }
 * ```
 *
 * In the example above, `Linux`, `Mac` and `Win` are concrete entries, which
 * may produce concrete records (such as a library, version, etc.). Meanwhile,
 * `Runtime` does not produce anything. It's just hosting other entries, affecting
 * their final alias.
 */
internal open class CatalogEntry : AbstractCatalogEntry() {

    /**
     * No records are produced by this entry.
     */
    override fun records(): Set<CatalogRecord> = emptySet()
}
