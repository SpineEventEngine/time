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

package io.spine.internal.catalog.model.given

import io.spine.internal.catalog.model.CatalogEntry

internal object VersionEntry : CatalogEntry() {
    override val version = "v0.0.1"
}

internal object LibraryEntry : CatalogEntry() {
    override val version = "l0.0.1"
    override val module = "com.company:lib"
}

internal object ExtraLibraryEntry : CatalogEntry() {
    override val version = "el0.0.1"

    @Suppress("unused")
    val core by lib("com.company:core-lib")
}

internal object PluginEntry : CatalogEntry() {
    override val version = "p0.0.1"
    override val id = "com.company.plugin"
}

@Suppress("MemberVisibilityCanBePrivate")
internal object BundleEntry : CatalogEntry() {
    override val version = "b0.0.1"
    override val module = "com.company:lib"
    val core by lib("com.company:core-lib")
    override val bundle = setOf(
        this,
        core,
        lib("runner", "com.company:runner-lib")
    )
}

@Suppress("MemberVisibilityCanBePrivate")
internal object ExtraBundleEntry : CatalogEntry() {
    override val version = "b0.0.1"
    override val module = "com.company:lib"

    val core by lib("com.company:core-lib")

    @Suppress("unused")
    val full by bundle(
        this,
        core,
        lib("runner", "com.company:runner-lib")
    )
}

internal object EmptyRootEntry : CatalogEntry()

internal object RootEntry : CatalogEntry() {

    override val version = "re0.0.0"

    @Suppress("unused")
    object FirstChild : CatalogEntry() {
        override val version = "fc0.0.0"
    }

    @Suppress("unused")
    object SecondChild : CatalogEntry() {
        override val version = "sc0.0.0"
    }

    @Suppress("unused")
    object ThirdChild : CatalogEntry() {

        override val version = "tc0.0.0"

        object GrandChild : CatalogEntry() {
            override val version = "gc0.0.0"
        }
    }
}

internal object DirectInheritingEntry : CatalogEntry() {
    override val version = "dvi0.0.1"

    @Suppress("unused")
    object Inheritor : CatalogEntry() {
        override val module = "com.company:lib"
    }
}

internal object IndirectInheritingEntry : CatalogEntry() {
    override val version = "ivi0.0.1"

    @Suppress("unused")
    object Separator : CatalogEntry() {
        object Inheritor : CatalogEntry() {
            override val module = "com.company:lib"
        }
    }
}

internal object ErroneousLibraryEntry : CatalogEntry() {
    @Suppress("unused")
    object Nested : CatalogEntry() {
        override val module = "com.company:lib"
    }
}

internal object ErroneousPluginEntry : CatalogEntry() {
    @Suppress("unused")
    object Nested : CatalogEntry() {
        override val id = "com.company:lib"
    }
}

internal object WithPluginEntry : CatalogEntry() {
    @Suppress("unused")
    internal object GradlePlugin : CatalogEntry() {
        override val version = "gp0.0.2"
        override val id = "my.plugin"
    }
}

internal object OuterEntry : CatalogEntry() {
    object Nested : CatalogEntry()
}
