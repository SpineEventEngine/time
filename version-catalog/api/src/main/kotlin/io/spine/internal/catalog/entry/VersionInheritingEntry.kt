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

import io.spine.internal.catalog.Alias
import io.spine.internal.catalog.CatalogRecord
import io.spine.internal.catalog.VersionNotation
import io.spine.internal.catalog.VersionRecord

/**
 * A skeleton implementation of a version entry, which can take the version
 * from the outer entry.
 *
 * From the outside it looks like the entry just "inherits" the version from
 * its parent.
 *
 * In order to understand why it's needed, consider the following snippet:
 *
 * ```
 * internal object Kotlin : LibraryEntry() {
 *     override val version = "1.6.21"          // libs.versions.kotlin
 *     override val module = "jetbrains:kotlin" // libs.kotlin
 *
 *     object Runtime : LibraryEntry() {
 *         override val version = Kotlin.version            // libs.versions.kotlin.runtime
 *         override val module = "jetbrains:kotlin-runtime" // libs.kotlin.runtime
 *     }
 * }
 * ```
 *
 * Both `Kotlin` and `Kotlin.Runtime` libraries will receive the same version,
 * declared in a single place. But for each library, an independent version
 * record will be created:
 *
 *  1. `libs.versions.kotlin`.
 *  2. `libs.versions.kotlin.runtime`.
 *
 * Thus, a local overriding (in settings file) of `libs.versions.kotlin` will
 * not affect the version of `Kotlin.Runtime` library. But, intuitively should.
 *
 * In contrast, when using an entry which extends this skeleton, there's no
 * need in manual declaration of the version in the nested entry. A version
 * inheriting entry can take the version from the outer entry on its own.
 *
 * Consider the same snippet, but with an entry which extends this class:
 *
 * ```
 * internal object Kotlin : LibraryEntry() {
 *     override val version = "1.6.21"              // libs.versions.kotlin
 *     override val module = "org.jetbrains:kotlin" // libs.kotlin
 *
 *     object Runtime : SomeVersionInheritingEntry() {
 *         override val module = "org.jetbrains:kotlin-runtime" // libs.kotlin.runtime
 *     }
 * }
 * ```
 *
 * Going this way, only `libs.versions.kotlin` will be generated and available
 * for a local override. Both `Kotlin` and `Kotlin.Runtime` libraries will
 * reference `libs.versions.kotlin` version. When this version is overridden,
 * both libraries will be affected as well.
 *
 * Although, such entries are not bound to use only an inherited version.
 * It is still possible to declare the version within this entry. But in case,
 * when neither this entry nor its outer one declares a version, an exception
 * will be thrown.
 *
 * See direct implementations: [PluginEntry], [DependencyEntry].
 */
open class VersionInheritingEntry : CatalogEntry(), VersionNotation {

    private companion object {
        const val FROM_PARENT = ""
    }

    /**
     * A version of this entry.
     *
     * When unspecified, the entry will try to use the version, declared in
     * the outer entry.
     *
     * Please note, a presence of the version is mandatory. And if neither this
     * entry nor its outer one declares a version, an exception will be thrown.
     */
    override val version: String = FROM_PARENT

    /**
     * When inheritors of this class need a version, they should use this reference.
     *
     * It is lazy by design. Otherwise, if this property is computed during class
     * initialization, we will never be able to handle a version, declared by
     * this entry on its own.
     *
     * In other words, making this property non-lazy eliminates a possibility
     * of a version declaration for this entry. It will be able only to inherit
     * the version from the outer entry.
     */
    protected val versionAlias: Alias by lazy { versionAlias() }

    override fun records(): Set<CatalogRecord> {

        if (version == FROM_PARENT) {
            return emptySet()
        }

        val version = VersionRecord(alias, version)
        return setOf(version)
    }

    private fun versionAlias(): Alias = when {
        version != FROM_PARENT -> alias
        outerEntry is VersionInheritingEntry -> outerEntry.versionAlias
        outerEntry is VersionNotation -> outerEntry.alias
        else -> throw IllegalStateException("Specify version in this entry or the outer entry!")
    }
}
