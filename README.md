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
 
## Using Spine Time in a Gradle project

To add a dependency to a Gradle project, please use the following:

```kotlin
dependencies {
    implementation("io.spine:spine-time:$version") 
}
```

In addition to the generated types and basic factory and calculation routines, the library 
provides converters between its types and Java Time and [`kotlinx-datetime`][kotlinx-datetime].
It is expected that an application code would perform the date/time calculations using Java Time or
[`kotlinx-datetime`][kotlinx-datetime].

[codecov]: https://codecov.io/gh/SpineEventEngine/time
[codecov-badge]: https://codecov.io/gh/SpineEventEngine/time/branch/master/graph/badge.svg
[license-badge]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat
[license]: http://www.apache.org/licenses/LICENSE-2.0

[java-time]: http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html                                                        
[kotlinx-datetime]: https://github.com/Kotlin/kotlinx-datetime
