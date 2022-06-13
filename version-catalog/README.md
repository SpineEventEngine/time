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
 4. Make an object inherit from `CatalogEntry`. 
 5. Perform all necessary declarations within this object.
 6. Publish a new version of the catalog.

Take a look on an example, which showcases usage of `CatalogEntry` API. Pay attention
to how entries are nested one into another. And how it reflects in their resulting accessors.

Source code of `Dummy` dependency:

```kotlin
internal object Dummy : CatalogEntry() {

   private const val group = "org.dummy.company"
   override val module = "$group:dummy-lib" // libs.dummy
   override val version = "1.0.0"           // libs.versions.dummy

   val core by lib("$group:dummy-core")     // libs.dummy.core
   val runner by lib("$group:dummy-runner") // libs.dummy.runner
   val api by lib("$group:dummy-api")       // libs.dummy.api

   // In bundles, you can reference entries (which declare module), extra
   // libraries or declare them in-place.

   override val bundle = setOf( // libs.bundles.dummy
      this,
      core, runner, api,
      lib("params", "$group:dummy-params"), // libs.dummy.params
      lib("types", "$group:dummy-types"),   // libs.dummy.types
   )

   // "GradlePlugin" - is a special entry name. "gradlePlugin" suffix will not
   // be put for a final plugin alias. Note, that in an example below, we have
   // this suffix for the version and module, and does not have for ID.

   object GradlePlugin : CatalogEntry() {
      override val version = "0.0.8"                 // libs.versions.dummy.gradlePlugin
      override val module = "$group:my-dummy-plugin" // libs.dummy.gradlePlugin
      override val id = "my-dummy-plugin"            // libs.plugins.dummy
   }

   object Runtime : CatalogEntry() {

      // When an entry does not override the version, it will try to fetch it
      // from the closest parental entry, which has one. For example, in this case,
      // all libraries within "Runtime" entry will have version = "1.0.0".

      val win by lib("$group:runtime-win")     // libs.dummy.runtime.win
      val mac by lib("$group:runtime-mac")     // libs.dummy.runtime.mac
      val linux by lib("$group:runtime-linux") // libs.dummy.runtime.linux

      object Bom : CatalogEntry() {
         override val version = "2.0.0"           // libs.versions.dummy.runtime.bom
         override val module = "$group:dummy-bom" // libs.dummy.runtime.bom
      }
   }

   // It's also possible to declare an extra bundle by a property delegate.
   // Just like with extra modules.

   val runtime by bundle( // libs.bundles.dummy.runtime
      Runtime.Bom,
      Runtime.win,
      Runtime.mac,
      Runtime.linux,
   )
}
```


## Overriding of items shipped by `SpineVersionCatalog`

Sometimes, it happens that a projects needs to override items, shipped by the catalog.
In most cases, it is needed to override one or more versions.

Currently, Gradle does not [provide](https://github.com/gradle/gradle/issues/20836) 
a clear way to perform overwrite for already created catalogs. Thus, we use approach
with creating a catalog directly in settings files. It preserves a possibility
of a local override.

Below are examples on how to override the items of `Dummy` dependency. Instead of
applying `SpineVersionCatalog`, `DummyVersionCatalog` is used, as we are going to
override `Dummy`'s items.

### Overriding of versions

In order to override a version you should declare it *before* applying the catalog.
A version, declared first always wins. All subsequent declarations of the same version
will be ignored by the builder.

In total, `Dummy` declares three versions.

Let's override them all:

```kotlin
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
           
            version("dummy", "2.0.0")              // Dummy.version
            version("dummy-gradlePlugin", "0.0.9") // Dummy.GradlePlugin.version
            version("dummy-runtime-bom", "3.0.0")  // Dummy.Runtime.Bom.version
           
            DummyVersionCatalog.useIn(this)
        }
    }
}
```

### Overriding of libraries, plugins and bundles

In order to override a library, plugin or bundle, one should declare it *after* 
applying the catalog. This is the opposite of what is done with versions.

When overriding libraries and plugins, a version should be specified in-place.

For example:

```kotlin
dependencyResolutionManagement {
   versionCatalogs {
      create("libs") {
         
         DummyVersionCatalog.useIn(this)
         
         library("dummy", "org.dummy.company:dummy-lib-patched:3.41-patched")           // Dummy.module + version
         library("dummy-gradlePlugin", "org.dummy.company:another-plugin:3.41-patched") // Dummy.GradlePlugin.module + version
         library("dummy-runtime-mac", "org.dummy.company:runtime-linux:3.41-patched")   // Dummy.Runtime.mac + version

         plugin("dummy", "my-dummy-plugin-patched").version("1.0.0-patched") // Dummy.GradlePlugin.id + version

         // In bundles, the passed list contains aliases of libraries. 
         bundle("dummy", listOf("dummy-runtime-bom", "dummy-runtime-win"))   // Dummy.bundle
         bundle("dummy-runtime", listOf("dummy", "dummy-core"))              // Dummy.runtime
      }
   }
}
```


## Modules structure

Within this PR, `spine-version-catalog` is a resident of `time` repository, but
it is a standalone project. Meaning, it has its own Gradle's `settings` file,
and doesn't relate anyhow to `time`.

`spine-version-catalog` consists of two modules:

1. `catalog` – provides a facade upon Gradle's provided `VersionCatalogBuilder`
and assembles `SpineVersionCatalog`, using that facade.

2. `func-test` – performs testing of the facade with a real instance of `VersionCatalogBuilder`.
To do that, the module does the following:

   1. Assembles a `dummy-catalog` with a single `Dummy` dependency and publishes
   it to Maven local. In order to declare `Dummy`, the module depends on `:catalog`, 
   which exposes the declarative facade.
   2. Makes `dummy-project` use `dummy-catalog` from Maven local. 
   3. Builds `dummy-project`. It has assertions in its build file. Those assertions verify
   the generated type-safe accessors to `Dummy` dependency. When any of assertions
   fails, the test fails accordingly.


## Details about Functional Testing

We have to do a true functional testing here, because Gradle does not provide 
a test fixture for `Settings`, as it does for `Project`. For this reason, we test 
it on a real Gradle project, with assertions right in a build file.

See [issue](https://github.com/gradle/gradle/issues/20807) for details.
