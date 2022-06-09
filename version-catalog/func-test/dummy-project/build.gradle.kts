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

import org.gradle.api.internal.catalog.ExternalModuleDependencyFactory
import org.gradle.api.internal.catalog.ExternalModuleDependencyFactory.BundleNotationSupplier
import org.gradle.api.internal.catalog.ExternalModuleDependencyFactory.VersionNotationSupplier

fun assert(actual: ExternalModuleDependencyFactory.DependencyNotationSupplier, expected: String) =
    assert(actual.asProvider(), expected)

fun assert(actual: VersionNotationSupplier, expected: String) = assert(actual.asProvider(), expected)

fun assert(actual: BundleNotationSupplier, expected: String) = assert(actual.asProvider(), expected)

fun assert(actual: Provider<*>, expected: String) = assert(actual.get().toString(), expected)

fun assert(actual: String, expected: String) {
    if (actual != expected) {
        throw IllegalStateException(
            "\nAssertion failed:" +
                    "\nExpected: $expected" +
                    "\n  Actual: $actual"
        )
    }
}

val group = "org.dummy.company"

assert(libs.versions.dummy, "1.0.0")
assert(libs.dummy, "$group:dummy-lib:1.0.0")

with(libs.dummy) {
    assert(core, "$group:dummy-core:1.0.0")
    assert(runner, "$group:dummy-runner:1.0.0")
    assert(api, "$group:dummy-api:1.0.0")
    assert(params, "$group:dummy-params:1.0.0")
    assert(types, "$group:dummy-types:1.0.0")
}

assert(
    libs.bundles.dummy, "[" +
            "$group:dummy-core:1.0.0, " +
            "$group:dummy-runner:1.0.0, " +
            "$group:dummy-api:1.0.0, " +
            "$group:dummy-params:1.0.0, " +
            "$group:dummy-types:1.0.0" +
            "]"
)

assert(libs.versions.dummy.gradlePlugin, "0.0.8")
assert(libs.dummy.gradlePlugin, "$group:my-dummy-plugin:0.0.8")
assert(libs.plugins.dummy, "my-dummy-plugin:0.0.8")

assert(libs.versions.dummy.runtime.bom, "2.0.0")
with(libs.dummy.runtime) {
    assert(win, "$group:runtime-win:1.0.0")
    assert(mac, "$group:runtime-mac:1.0.0")
    assert(linux, "$group:runtime-linux:1.0.0")
    assert(bom, "$group:dummy-bom:2.0.0")
}

assert(
    libs.bundles.dummy.runtime,
    "[" +
            "$group:dummy-lib:1.0.0, " +
            "$group:dummy-bom:2.0.0, " +
            "$group:runtime-win:1.0.0, " +
            "$group:runtime-mac:1.0.0, " +
            "$group:runtime-linux:1.0.0" +
            "]"
)

assert(libs.versions.dummy.tools, "3.0.0")
