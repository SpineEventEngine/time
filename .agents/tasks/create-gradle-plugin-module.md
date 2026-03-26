# Task: Create Gradle Plugin Module

- We need to create a Gradle plugin module in the project.
- The module should be named `gradle-plugin`.

## Class names
- Gradle plugin class: `io.spine.tools.time.gradle.TimeGradlePlugin`
- Plugin extension class: `io.spine.tools.time.gradle.TimeGradleExtension`

## Plugin ID
- `io.spine.time`

## Maven coordinates for the plugin
- Group ID: `io.spine.tools`
- Artifact ID: `time-gradle-plugin`

## Feature: adding dependencies on Spine Time modules
- When applied to a project, the plugin should add dependencies on the following Spine Time modules:
  - `io.spine:spine-time` — always.
  - `io.spine:spine-time-java` — when the extension property `useJavaExtensions` is set to `true`.
  - `io.spine:spine-time-kotlin` — when the extension property `useKotlinExtensions` is set to `true`.

The version of the dependencies is obtained from the `Meta` class which should be added
in the same project with the `TimeGradlePlugin` class.

Take the Validation Gradle Plugin as an example of using the `Meta` class to obtain
the version of the dependencies. The code is available at this directory: `../validation/gradle-plugin`.

Please also take into account that data for the `Meta` class is written using the `artifactMeta`
Gradle plugin. Use `../validation/gradle-plugin/build.gradle.kts` as an example of how
to configure the `artifactMeta` plugin for the `Meta` class.
