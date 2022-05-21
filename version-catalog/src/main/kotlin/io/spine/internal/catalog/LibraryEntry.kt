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

package io.spine.internal.catalog

internal open class LibraryEntry : VersionEntry(), LibraryEntryDsl {

    private companion object {
        const val GAV_SEPARATOR = ':'
    }

    override val module: String? = null
    override val bundle: Set<LibraryAlias>? = null

    override fun initialize() {
        super.initialize()
        module?.let { module(alias.relative, it) }
        bundle?.let { bundle(alias.relative, it) }
    }

    override fun module(value: String): PropertyDelegate<LibraryAlias> =
        delegate { property ->
            module(property.name, value)
        }

    override fun module(relativeAlias: String, value: String): LibraryAlias {
        check(version != null) { "A module can't be declared unless its version is specified!" }
        val alias = resolve(relativeAlias)
        val (group, artifact) = value.splitBy(GAV_SEPARATOR)
        builder { library(alias.absolute, group, artifact).version(version!!) }
        return alias.toLibrary()
    }

    private fun String.splitBy(separator: Char) =
        Pair(substringBefore(separator), substringAfter(separator))

    override fun lib(module: String, version: VersionAlias): PropertyDelegate<LibraryAlias> =
        delegate { property ->
            val alias = resolve(property.name)
            val (group, artifact) = module.splitBy(GAV_SEPARATOR)
            builder { library(alias.absolute, group, artifact).versionRef(version.absolute) }
            alias.toLibrary()
        }

    override fun lib(gav: String): PropertyDelegate<LibraryAlias> =
        delegate { property ->
            val alias = resolve(property.name)
            builder { library(alias.absolute, gav) }
            alias.toLibrary()
        }

    override fun bundle(vararg libs: LibraryAlias): PropertyDelegate<BundleAlias> =
        delegate { property ->
            bundle(property.name, libs.toSet())
        }

    private fun bundle(relativeAlias: String, libs: Set<LibraryAlias>): BundleAlias {
        val alias = resolve(relativeAlias)
        builder { bundle(alias.absolute, libs.map { it.absolute }) }
        return alias.toBundle()
    }
}
