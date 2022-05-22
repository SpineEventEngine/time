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

import io.spine.internal.PropertyDelegate
import io.spine.internal.delegate

internal open class PluginEntry : LibraryEntry(), PluginEntryDsl {

    override val id: String? = null

    override fun initialize() {
        super.initialize()
        id?.let { plugin("", it) }
    }

    private fun plugin(relativeAlias: String, id: String): PluginAlias {
        val alias = resolve(relativeAlias)
        val versionAlias = if(version != null) alias else fetchVersionFromParent()
        val versionRef = versionAlias?.absolute ?: throw IllegalStateException("A module can't be declared unless its version is specified!")
        builder { plugin(alias.absolute, id).versionRef(versionRef) }
        return alias.toPlugin()
    }

    private fun fetchVersionFromParent(): EntryReference? {
        val outer = javaClass.enclosingClass?.kotlin?.objectInstance
        val versionRef = if (outer is VersionEntrySketch) outer.alias else null
        return versionRef
    }
}
