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

import io.spine.internal.catalog.SpineVersionCatalog

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath("io.spine.internal:spine-version-catalog:+")
    }
}

apply {
    plugin("io.spine.internal.version-catalog")
}

val spineDependencies = extensions.getByType<SpineVersionCatalog>()

dependencyResolutionManagement {
    versionCatalogs {

        // The plugin doesn't create a catalog on its own. It just provides
        // an extension, that can execute code upon `VersionCatalogBuilder`.
        //
        // It is so because we want to preserve a possibility of overwrite.
        // Currently, Gradle does not provide a clear way to do overwrite.
        // When a lib is added to a catalog, it can not be overwritten.
        // The subsequent attempts to add the lib lead to a silent nothing.
        //
        // Thus, to overwrite a lib or version you should declare it first.
        //
        // See the issue: https://github.com/gradle/gradle/issues/20836

        create("libs") {

            // An example of how to override versions.
            //
            // Below we override the versions of Kotlin for our build logic.
            //
            // Anyway, build scripts are executed by Gradle's embedded Kotlin.
            // And writing build logic with different Kotlin makes little sense.
            //
            // + It gets rid of a big warning block.

            version("kotlin", "1.5.31")
            version("kotlinX-coroutines", "1.5.2")

            spineDependencies.useIn(this)
        }
    }
}
