# Project: Spine Time

## Overview

Spine Time is a library of Protobuf-based date and time types for describing
dates and times in a business model. In addition to the `Timestamp` and
`Duration` types built into Protobuf, it provides calendar-oriented types such
as `LocalDate`, `LocalTime`, `LocalDateTime`, `YearMonth`, `OffsetTime`,
`OffsetDateTime`, `ZonedDateTime`, `ZoneId`, and `ZoneOffset`, following the
conventions of [Java Time][java-time]. It is part of the Spine SDK: projects
based on Spine CoreJvm get it configured automatically, and it can also be used
as a standalone library via the `io.spine.time` Gradle plugin.

## Architecture

Role in the org: a **library** with a companion **Gradle plugin**.

- The time types are declared in Protobuf (`time/src/main/proto/spine/time`) and
  generated for Java and Kotlin. Hand-written mixin interfaces (`*Temporal`,
  `*Mixin`) and utility classes (`LocalDates`, `Months`, `ZoneIds`, …) add
  behavior on top of the generated messages.
- Conversions to and from Java Time and [`kotlinx-datetime`][kotlinx-datetime]
  are the primary public API: static `Xs.of(...)` / `Xs.toJavaTime(value)`
  utilities, instance `value.toJavaTime()` mixin methods, and Kotlin
  `toKotlin*` / `toProto*` extensions. Converting a **default** (unset) message
  that carries no meaningful date or zone is rejected with a clear exception
  rather than producing a misleading value.
- Modules: `time` (core types and the Java/Kotlin API), `time-java` and
  `time-kotlin` (language-specific extensions), `gradle-plugin` (the
  `io.spine.time` plugin), `validation` and `validation-tests` (support for the
  `(when)` field option), and `testlib`. Cross-module integration tests live in
  the standalone `tests` build. Versions `2.*` build with Java 17.

Read [`.agents/guidelines/jvm-project.md`](../.agents/guidelines/jvm-project.md)
for build stack, coding style, tests, and versioning.

[java-time]: https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/package-summary.html
[kotlinx-datetime]: https://github.com/Kotlin/kotlinx-datetime
