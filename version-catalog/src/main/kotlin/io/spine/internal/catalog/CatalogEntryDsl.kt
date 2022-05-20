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

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

internal typealias PropertyDelegate<T> = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>>

private const val GAV_SEPARATOR = ':'

internal interface CatalogEntryDsl {

    val version: String?
    val module: String?
    val bundle: Set<LibraryAlias>?
    val id: String?

    fun module(group: String, artifact: String): PropertyDelegate<LibraryAlias>

    fun module(value: String): PropertyDelegate<LibraryAlias> = value.run {
        val group = substringBefore(GAV_SEPARATOR)
        val artifact = substringAfter(GAV_SEPARATOR)
        module(group, artifact)
    }

    fun lib(group: String, artifact: String, version: VersionAlias): PropertyDelegate<LibraryAlias>

    fun lib(gav: String): PropertyDelegate<LibraryAlias>

    fun lib(module: String, version: VersionAlias): PropertyDelegate<LibraryAlias> = module.run {
        val group = substringBefore(GAV_SEPARATOR)
        val artifact = substringAfter(GAV_SEPARATOR)
        lib(group, artifact, version)
    }

    fun bundle(vararg libs: LibraryAlias): PropertyDelegate<BundleAlias>

    fun plugin(id: String, version: VersionAlias): PropertyDelegate<PluginAlias>

    fun plugin(id: String, version: String): PropertyDelegate<PluginAlias>

}
