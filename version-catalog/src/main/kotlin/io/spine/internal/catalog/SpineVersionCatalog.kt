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

import org.gradle.api.initialization.dsl.VersionCatalogBuilder

/**
 * An atomic unit which contributes to Version Catalog.
 *
 * Usually, it's an object which represents one of the following:
 *
 *  1. A single library itself.
 *  2. Group of relates libraries.
 *  3. One or more libraries provided by the same vendor.
 *
 * Such a unit can add one or more of the following items to the catalog:
 *
 *  1. [versioning].
 *  2. [lib].
 *  3. [bundle].
 *  4. [plugin].
 *
 *  Then, a resulted entry can be [added][addTo] to Version Catalog using
 *  the given [VersionCatalogBuilder].
 *
 *  This class relies on an assumption that all its subclasses are indeed object
 *  declarations (singletons). And in order to avoid imperative code, the class
 *  utilizes property delegation to declare items. Property name together with
 *  object's name form a resulting alias for an item in the catalog. Consider
 *  the following example.
 *
 *  The entry below:
 *
 *  ```
 *  internal object Kotlin : VersionCatalogEntry() {
 *      private const val version = "1.6.21"
 *      val kotlin by version(version)
 *
 *      val reflect by lib("org.jetbrains.kotlin:kotlin-reflect:$version")
 *      val stdLib by lib("org.jetbrains.kotlin:kotlin-stdlib:$version")
 *      val stdLibCommon by lib("org.jetbrains.kotlin:kotlin-stdlib-common:$version")
 *      val stdLibJdk8 by lib("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version")
 *  }
 *  ```
 *
 *  Results in the next accessors:
 *
 *  ```
 *  // When property and object's names are the same, entry will not
 *  // duplicate it. It allows avoiding `libs.version.kotlin.kotlin`.
 *
 *  libs.versions.kotlin
 *
 *  libs.kotlin.reflect
 *  libs.kotlin.stdLib
 *  libs.kotlin.stdLibCommon
 *  libs.kotlin.stdLibJdk8
 *  ```
 *
 *  Kotlin, as expected, doesn't allow two properties have the same name within
 *  a single scope. Out of this stems a restriction. In order to declare the
 *  same alias for library, version, bundle or plugin, one should you nested
 *  classes to demarcate scopes. By convention, libraries are still better to
 *  declare on the top scope. It would mirror the way, they are available from
 *  the generated accessors. Consider the next example.
 *
 *  The entry below:
 *
 *  ```
 *  internal object Kotlin : VersionCatalogEntry() {
 *
 *      private const val version = "1.6.21"
 *      val kotlin by lib("org.jetbrains.kotlin:kotlin:$version")

 *      object Version {
 *          val kotlin by version(version)
 *      }
 *
 *      object Plugin {
 *          val kotlin by plugin("org.jetbrains.kotlin", version)
 *      }
 *  }
 *  ```
 *
 * Results in the next accessors:
 *
 * ```
 * libs.kotlin
 * libs.versions.kotlin
 * libs.plugins.kotlin
 * ```
 *
 * It is also allowed to have nested entries. Which are taken into account when
 * a final alias is resolved. Consider the next example.
 *
 * The entry below:
 *
 * ```
 * internal object Kotlin : VersionCatalogEntry() {
 *
 *     private const val version = "1.6.21"
 *     val reflect by lib("org.jetbrains.kotlin:kotlin-reflect:$version")
 *     val runtime by lib("org.jetbrains.kotlin:kotlin-runtime:$version")
 *
 *     object StdLib : VersionCatalogEntry() {
 *         val stdLib by lib("org.jetbrains.kotlin:kotlin-stdlib:$version")
 *         val common by lib("org.jetbrains.kotlin:kotlin-stdlib-common:$version")
 *         val jdk8 by lib("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version")
 *     }
 * }
 * ```
 *
 * Results in the next accessors:
 *
 * ```
 * libs.kotlin.reflect
 * libs.kotlin.runtime
 *
 * libs.kotlin.stdLib
 * libs.kotlin.stdLib.common
 * libs.kotlin.stdLib.jdk8
 * ```
 */
open class SpineVersionCatalog {

    /**
     * Registers Spine dependencies in the given version catalog.
     */
    @Suppress("unused")
    fun useIn(catalog: VersionCatalogBuilder) {
        val locator = VersionCatalogEntriesLocator.forPackage("io.spine.internal.catalog.entry")
        val newEntries = locator.find()
        newEntries.forEach { it.accept(catalog) }
    }
}
