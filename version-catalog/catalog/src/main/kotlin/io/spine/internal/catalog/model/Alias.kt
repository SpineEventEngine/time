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

package io.spine.internal.catalog.model

/**
 * A name, by which an item is known in a version catalog.
 *
 * It consists of one or more camel cased words separated by a hyphen.
 *
 * For example: `kotlinX-coroutines-gradlePlugin`.
 *
 * ### Role of aliases in the catalog
 *
 * A version catalog itself consists of four sections: versions, libraries, plugins
 * and bundles (sets of libraries). Thus, an alias can denote four types of items.
 * Within each section, an alias should be unique.
 *
 * Aliases perform two functions:
 *
 *  1. Navigation. By an alias, one can locate and access an item in the catalog.
 *  2. Referencing. One item in a version catalog can use another item, and this
 *     linkage is done via aliases.
 *
 * ### Mapping to the generated accessors
 *
 * Below is an example of how aliases are mapped to the generated type-safe
 * accessors of the catalog.
 *
 * Let's suppose, a catalog is named `libs`:
 *
 * ```
 * "kotlinX-coroutines" => libs.kotlinX.coroutines
 * "kotlinX-coroutines-gradlePlugin" => libs.kotlinX.coroutines.gradlePlugin
 * "kotlinX-runtime-jvm" => libs.kotlinX.runtime.jvm
 * "kotlinX-runtime-clr" => libs.kotlinX.runtime.clr
 * ```
 *
 * Depending on which type of item an alias points, the resulting accessor
 * may have additional prefixes.
 *
 * Below are an accessor patterns for different items:
 *
 *  1. Library: `{catalog name}.{alias}`.
 *  2. Version: `{catalog name}.versions.{alias}`.
 *  3. Plugin : `{catalog name}.plugins.{alias}`.
 *  4. Bundle : `{catalog name}.bundles.{alias}`.
 */
internal typealias Alias = String
