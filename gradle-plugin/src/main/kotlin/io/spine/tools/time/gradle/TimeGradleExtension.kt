/*
 * Copyright 2026, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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

package io.spine.tools.time.gradle

import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.provider.Property

/**
 * The extension added by [TimeGradlePlugin] to configure the Spine Time library.
 */
public abstract class TimeGradleExtension @Inject public constructor(project: Project) {

    /**
     * Tells if the `spine-time-java` module should be added as a dependency.
     *
     * Default value is `false`.
     */
    public val useJavaExtensions: Property<Boolean> =
        project.objects.property(Boolean::class.java)

    /**
     * Tells if the `spine-time-kotlin` module should be added as a dependency.
     *
     * Default value is `false`.
     */
    public val useKotlinExtensions: Property<Boolean> =
        project.objects.property(Boolean::class.java)

    /**
     * Tells if the `time-testlib` module should be added as a `testImplementation`
     * dependency.
     *
     * Default value is `false`.
     */
    public val useTestLib: Property<Boolean> =
        project.objects.property(Boolean::class.java)

    init {
        useJavaExtensions.convention(false)
        useKotlinExtensions.convention(false)
        useTestLib.convention(false)
    }

    public companion object {

        /**
         * The name of the extension as it appears in the Gradle DSL.
         */
        public const val NAME: String = "time"
    }
}
