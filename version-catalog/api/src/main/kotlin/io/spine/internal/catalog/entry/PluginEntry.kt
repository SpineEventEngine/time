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

import io.spine.internal.catalog.Alias
import io.spine.internal.catalog.CatalogRecord
import io.spine.internal.catalog.LibraryRecord
import io.spine.internal.catalog.PluginNotation
import io.spine.internal.catalog.PluginRecord
import io.spine.internal.catalog.VersionRecord

/**
 * A catalog entry, which is used to declare a Gradle plugin.
 *
 * Only object declarations are meant to inherit from this class.
 *
 * Below is an example of how to declare a plugin using this entry:
 *
 * ```
 * internal object MyPlugin : PluginEntry() {
 *     override val version = "1.0.0"
 *     override val module = "org.my.company:my-gradle-plugin"
 *     override val id = "org.my.company.plugin"
 * }
 * ```
 *
 * Specifying of [version] is optional, when it can be inherited from
 * the outer entry.
 *
 * An example with an inherited version:
 *
 * ```
 * internal object MyLib : VersionEntry() {
 *     override val version = "1.2.3"
 *
 *     object GradlePlugin : PluginEntry() {
 *         override val module = "org.my.company:my-gradle-plugin"
 *         override val id = "org.my.company.plugin"
 *     }
 * }
 * ```
 *
 * Also, there's a special treatment for plugin entries named "GradlePlugin".
 * For them, "gradlePlugin" suffix will not be appended to a final plugin's alias.
 *
 * Consider the following example:
 *
 * ```
 * internal object MyLib : CatalogEntry() {
 *     object GradlePlugin : PluginEntry() {
 *         override val version = "1.2.3"               // libs.versions.myLib.gradlePlugin
 *         override val module = "my.company:my-plugin" // libs.myLib.gradlePlugin
 *         override val id = "org.my.company.plugin"    // libs.plugins.myLib
 *     }
 * }
 * ```
 *
 * In the example above, the side comments demonstrate the generated accessors.
 * The version and module have `gradlePlugin` suffix, while the plugin not.
 * It is done so in order not to repeat yourself in naming. Otherwise, we would
 * come up with this: `libs.plugins.myLib.gradlePlugin`.
 */
abstract class PluginEntry : AbstractVersionInheritingEntry(), PluginNotation {

    private val pluginAlias: Alias by lazy { pluginAlias() }

    /**
     * Always produces [PluginRecord] and [LibraryRecord].
     *
     * Optionally, it can produce [VersionRecord] if the entry declares it
     * on its own.
     */
    override fun records(): Set<CatalogRecord> {
        val result = mutableSetOf<CatalogRecord>()

        val optionalVersion = super.records()
        result.addAll(optionalVersion)

        val library = LibraryRecord(alias, module, versionAlias)
        result.add(library)

        val record = PluginRecord(pluginAlias, id, versionAlias)
        result.add(record)

        return result
    }

    private fun pluginAlias(): Alias =
        if (alias.endsWith("gradlePlugin")) alias.substringBeforeLast('-') else alias
}
