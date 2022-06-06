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

/**
 * A name, by which an item is known in a version catalog.
 *
 * Each item within the catalog has its unique alias.
 *
 * Aliases perform two functions:
 *
 *  1. Navigation. By an alias, one can locate and access an item in the catalog.
 *  2. Referencing. One item in a version catalog can use another item, and this
 *     linkage is done via aliases.
 *
 * Please, consider an example of how aliases are mapped to the generated
 * type-safe accessors of the catalog.
 *
 * ```
 * "kotlinx-coroutines" => libs.kotlin.coroutines
 * "kotlinx-coroutines-gradlePlugin" => libs.kotlin.coroutines.gradlePlugin
 * "kotlinx-coroutines-runtime-jvm" => libs.kotlin.runtime.jvm
 * "kotlinx-coroutines-runtime-clr" => libs.kotlin.runtime.clr
 * ```
 */
internal typealias Alias = String
