# SpineVersionCatalog

Represents a set of dependencies used in Spine-related projects. It's assembled 
and published in a form of Gradle's [Version Catalog](https://docs.gradle.org/current/userguide/platforms.html#sec:sharing-catalogs).


## Usage example

In order to use this catalog, one should perform the following:

 1. Put `spine-version-catalog` library on a classpath of `settings.gradle.kts` file.
 2. Create a new version catalog. `libs` is a conventional name to go with.
 3. Apply `SpineVersionCatalog` to a newly created catalog.

Below is an example of how to obtain this catalog in the project.

In `settings.gradle.kts` file of the project:

```kotlin
import io.spine.internal.catalog.SpineVersionCatalog

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.spine.internal:spine-version-catalog:2.0.0")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            SpineVersionCatalog.useIn(this)
        }
    }
}
```


## Adding a new dependency to the catalog

In order to add a new dependency to this catalog, perform the following steps:

 1. Go to `catalog` module.
 2. Open `io.spine.internal.catalog.entry` package.
 3. Create a new file there, which contains an object declaration, named after 
a dependency, that is being added.
 4. Make an object inherit from one of the following entries, depending on what 
a dependency represents:
    1. `VersionEntry` for a bare version.
    2. `LibraryEntry` for a single library.
    3. `PluginEntry` for a single Gradle plugin.
    4. `DependnecyEntry` for complex dependencies, which may contain several modules,
    plugins or bundles.
 5. Publish a new version of the catalog.

Take a look on an example, which showcases usage of all entries in a single place.
Pay attention to how entries are nested one into another. And how it reflects in
their resulting accessors.

Source code of `Dummy` dependency:

```kotlin
internal object Dummy : DependencyEntry() {

    private const val group = "org.dummy.company"
    override val module = "$group:dummy-lib" // libs.dummy
    override val version = "1.0.0"           // libs.versions.dummy

    val core by lib("$group:dummy-core")     // libs.dummy.core
    val runner by lib("$group:dummy-runner") // libs.dummy.runner
    val api by lib("$group:dummy-api")       // libs.dummy.api

    // In bundles, you can reference already declared libs,
    // or create them in-place.

    override val bundle = setOf( // libs.bundles.dummy
        core, runner, api,
        lib("params", "$group:dummy-params"), // libs.dummy.params
        lib("types", "$group:dummy-types"),   // libs.dummy.types
    )

    // "GradlePlugin" - is a special entry name for `PluginEntry`.
    // For plugin entries with this name, the facade will not put "gradlePlugin"
    // suffix for a plugin's id. Note, that we have this suffix for the version
    // and module, and does not have for id.

    object GradlePlugin : PluginEntry() {
        override val version = "0.0.8"                 // libs.versions.dummy.gradlePlugin
        override val module = "$group:my-dummy-plugin" // libs.dummy.gradlePlugin
        override val id = "my-dummy-plugin"            // libs.plugins.dummy
    }

    object Runtime : DependencyEntry() {

        // When an entry does not override the version, it is taken from
        // the outer entry. For example, in this case, all libs within "Runtime"
        // entry will have "1.0.0".

        val win by lib("$group:runtime-win")     // libs.dummy.runtime.win
        val mac by lib("$group:runtime-mac")     // libs.dummy.runtime.mac
        val linux by lib("$group:runtime-linux") // libs.dummy.runtime.linux

        object BOM : LibraryEntry() {
            override val version = "2.0.0"           // libs.versions.dummy.runtime.bom
            override val module = "$group:dummy-bom" // libs.dummy.runtime.bom
        }
    }

    // A library that is declared as `object SomeLib : LibraryEntry()` can be
    // referenced as well as the one declared by `lib()` delegate.

    val runtime by bundle( // libs.bundles.dummy.runtime
        Runtime.BOM,
        Runtime.win,
        Runtime.mac,
        Runtime.linux,
    )

    // It is also possible to declare just a bare version.

    object Tools : VersionEntry() {
        override val version = "3.0.0" // libs.versions.dummy.tools
    }
}
```


## Modules structure

Within this PR, `spine-version-catalog` is a resident of `time` repository, but
it is a standalone project. Meaning, it has its own Gradle's `settings` file,
and doesn't relate anyhow to `time`.

`spine-version-catalog` consists of several modules:

1. `api` – represents a facade upon Gradle's provided `VersionCatalogBuilder`. 
This module hides an imperative nature of the builder, and, instead, provides
a declarative API to declare dependencies using Kotlin objects.

2. `catalog` – contains all dependencies, declared using the declarative `api`. 
The module publishes `SpineVersionCatalog`.

3. `func-test` – performs testing of `api` with a real `VersionCatalogBuilder`.
To do that, the module does the following:

   1. Assembles a `dummy-catalog` with a single `Dummy` dependency and publishes
   it to Maven local.
   2. Makes `dummy-project` use `dummy-catalog` from Maven local. 
   3. Builds `dummy-project`. It has assertions in its build file. Those assertions verify
   the generated type-safe accessors to `Dummy` dependency. When any of assertions
   fails, the test fails accordingly.


## Details about Functional Testing

`func-test` module sets the next dependencies for `test` task:

```kotlin
test {
    dependsOn(
        ":api:publishToMavenLocal",
        ":func-test:dummy-catalog:publishToMavenLocal"
    )
}
```

It is so, because `dummy-project` (which the test builds), fetches `dummy-catalog` 
from Maven local. Which, in turn, depends on `api` module. Thus, we need them both in Maven local.

We have to do a true functional testing here, because Gradle does not provide 
a test fixture for `Settings`, as it does for `Project`. For this reason, we test 
it on a real Gradle project, with assertions right in a build file.

See [issue](https://github.com/gradle/gradle/issues/20807) for details.