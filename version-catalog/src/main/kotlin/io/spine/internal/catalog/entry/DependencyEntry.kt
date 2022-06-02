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

import io.spine.internal.catalog.AlwaysReturnDelegate
import io.spine.internal.catalog.BundleNotation
import io.spine.internal.catalog.BundleRecord
import io.spine.internal.catalog.CatalogRecord
import io.spine.internal.catalog.DependencyNotation
import io.spine.internal.catalog.LibraryNotation
import io.spine.internal.catalog.LibraryRecord
import io.spine.internal.catalog.delegate

internal abstract class DependencyEntry : AbstractVersionInheritingEntry(), DependencyNotation {

    private val standaloneLibs = mutableSetOf<LibraryNotation>()
    private val standaloneBundles = mutableSetOf<BundleNotation>()

    override fun records(): Set<CatalogRecord> {
        val result = mutableSetOf<CatalogRecord>()

        val optionalVersion = super.records()
        result.addAll(optionalVersion)

        if (module != null) {
            val library = LibraryRecord(alias, module!!, versionAlias)
            result.add(library)
        }

        if (bundle != null) {
            val aliases = bundle!!.map { it.alias }.toSet()
            val bundle = BundleRecord(alias, aliases)
            result.add(bundle)
        }

        val extraBundles = standaloneBundles.map { record(it) }
        result.addAll(extraBundles)

        val extraLibraries = standaloneLibs.map { record(it) }
        result.addAll(extraLibraries)

        return result
    }

    private fun record(notation: BundleNotation): BundleRecord {
        val aliases = notation.bundle.map { it.alias }.toSet()
        val record = BundleRecord(notation.alias, aliases)
        return record
    }

    private fun record(notation: LibraryNotation): LibraryRecord {
        val record = LibraryRecord(notation.alias, notation.module, versionAlias)
        return record
    }

    override fun lib(name: String, module: String): LibraryNotation {
        val thisEntryAlias = this.alias
        val libAlias = if (thisEntryAlias.endsWith(name)) thisEntryAlias
                       else "$thisEntryAlias-$name"

        val notation = object : LibraryNotation {
            override val alias: String = libAlias
            override val version: String = "" // will be taken from this entry
            override val module: String = module
        }

        standaloneLibs.add(notation)
        return notation
    }

    override fun lib(module: String): AlwaysReturnDelegate<LibraryNotation> =
        delegate { property -> lib(property.name, module) }

    override fun bundle(vararg libs: LibraryNotation): AlwaysReturnDelegate<BundleNotation> =
        delegate { property ->
            val thisEntryAlias = this.alias
            val bundleAlias = "$thisEntryAlias-${property.name}"

            val notation = object : BundleNotation {
                override val alias: String = bundleAlias
                override val bundle: Set<LibraryNotation> = libs.toSet()
            }

            standaloneBundles.add(notation)
            notation
        }
}
