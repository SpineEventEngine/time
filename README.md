# Spine Time: Protobuf-based Date/Time types

[![Ubuntu build][ubuntu-build-badge]][gh-actions]
[![codecov][codecov-badge]][codecov] &nbsp;
[![license][license-badge]][license]

[gh-actions]: https://github.com/SpineEventEngine/time/actions
[ubuntu-build-badge]: https://github.com/SpineEventEngine/time/actions/workflows/build-on-ubuntu.yml/badge.svg


In addition to `Timestamp` and `Duration` natively available from Protobuf, the Spine Time library
provides a set of data types for describing date and time in a business model.

The types provided by this library follow the conventions offered by [Java Time][java-time].

## Supported programming languages

The library currently supports Java and Kotlin (Protobuf DSL and
compatibility with [`kotlinx-datetime`][kotlinx-datetime]).

The versions `1.*` are built using Java 8.

The versions `2.*` are built with Java 17.
Therefore, consumer projects should aim for Java 17+ to use them.

## Integration with Spine CoreJvm

Projects based on the [Spine CoreJvm][core-jvm] library do not need to configure Time manually.
The [CoreJvm Compiler][core-jvm-compiler] automatically adds and configures Spine Time, including
the `(when)` validation support.

The sections below apply only when using Spine Time as a **standalone** library, without CoreJvm.

## Using the Time Gradle plugin

The recommended way to add Spine Time to a standalone project is via the `io.spine.time` Gradle
plugin. Apply it after a JVM language plugin (`java`, `java-library`, or `kotlin("jvm")`):

```kotlin
plugins {
    id("io.spine.time")
}
```

The plugin automatically adds `io.spine:spine-time` as an `implementation` dependency.

### Optional modules

Use the `time` extension block to opt in to additional modules:

```kotlin
spine {
    time {
        useJavaExtensions.set(true)    // adds `spine-time-java` (Java Time converters)
        useKotlinExtensions.set(true)  // adds `spine-time-kotlin` (`kotlinx-datetime` converters)
        useTestLib.set(true)           // adds `time-testlib` as `testImplementation`
    }
}
```

All three flags default to `false`.

### Manual dependency

If you prefer to manage the dependency directly rather than through the plugin:

```kotlin
dependencies {
    implementation("io.spine:spine-time:$version")
}
```

## Validating time fields with `(when)`

The `(when)` Protobuf field option constrains a time-valued field so that it must hold
a value either in the past or in the future.

It applies to:

- `google.protobuf.Timestamp`
- Any type from the `spine.time` package (e.g. `LocalDateTime`, `ZonedDateTime`)
- Repeated and map fields of the above types

### Enabling validation

The `(when)` option requires the [Spine Validation][validation] Gradle plugin. The `io.spine.time`
plugin automatically registers the `time-validation` module on the compiler classpath when
`io.spine.validation` is also applied:

```kotlin
plugins {
    id("io.spine.validation")
    id("io.spine.time")
}
```

### Usage

```protobuf
import "spine/time_options.proto";

message ScheduleMeeting {
    spine.time.ZonedDateTime start = 1 [(when).in = FUTURE];
    spine.time.ZonedDateTime end   = 2 [(when).in = FUTURE];
}

message AuditRecord {
    google.protobuf.Timestamp occurred_at = 1 [(when).in = PAST];
}
```

The `Time` enum accepts two values:

| Value    | Meaning                        |
|----------|--------------------------------|
| `PAST`   | The field value must be in the past   |
| `FUTURE` | The field value must be in the future |

### Custom error messages

Supply a custom message via `error_msg`. The following placeholders are available:

- `${field.path}` — the field path
- `${field.value}` — the field value
- `${field.type}` — the fully qualified name of the field type
- `${parent.type}` — the fully qualified name of the validated message
- `${when.in}` — the restriction, either `"past"` or `"future"`

```protobuf
google.protobuf.Timestamp scheduled_at = 1 [(when) = {
    in: FUTURE,
    error_msg: "The meeting must be scheduled in the future, but got `${field.value}`."
}];
```

[codecov]: https://codecov.io/gh/SpineEventEngine/time
[codecov-badge]: https://codecov.io/gh/SpineEventEngine/time/branch/master/graph/badge.svg
[license-badge]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat
[license]: http://www.apache.org/licenses/LICENSE-2.0

[core-jvm]: https://github.com/SpineEventEngine/core-jvm/
[core-jvm-compiler]: https://github.com/SpineEventEngine/core-jvm-compiler/
[java-time]: http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html
[kotlinx-datetime]: https://github.com/Kotlin/kotlinx-datetime
[validation]: https://github.com/SpineEventEngine/validation
