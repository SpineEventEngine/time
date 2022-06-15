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

import java.nio.file.Paths
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

/**
 * Verifies the generated type-safe accessors for `Dummy` dependency.
 *
 * `Dummy` is an imaginary dependency, which exists for two reasons:
 *
 *  1. Showcasing API for dependency declarations.
 *  2. Functional testing in conditions, which are very close to real life.
 *
 * `DummyVersionCatalog` provides a set of actions upon Gradle-provided `VersionCatalogBuilder`.
 * These actions fill up the passed catalog with records. And, as for now, there's
 * no any other legitimate way, except for a true functional test, to check whether:
 *
 *  1. Those actions are successfully executed upon a real instance of the builder.
 *  2. A resulted catalog, assembled from the builder, contains the expected items.
 *
 * See issue in Gradle: https://github.com/gradle/gradle/issues/20807
 */
@DisplayName("`DummyVersionCatalog` should")
class DummyVersionCatalogTest {

    /**
     * Triggers a `dummy-project` which uses a `dummy-catalog`.
     *
     * The project fetches `DummyVersionCatalog` from Maven local. Thus, the catalog
     * should be published to Maven local in advance. See README file for details.
     *
     * A build file of `dummy-project` has assertions upon the generated accessors
     * to `Dummy` dependency. When any of assertions fails, the build fails as well,
     * making the test not passed.
     */
    @Test
    fun `fill up an existing version catalog`() {
        val dummyProject = Paths.get("dummy-project").toFile()
        val runner = GradleRunner.create()
            .withProjectDir(dummyProject)
            .withArguments("help", "--stacktrace")

        assertDoesNotThrow {
            runner.build()
        }
    }
}
