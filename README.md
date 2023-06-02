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

The library currently supports Java, Kotlin (Protobuf DSL), and JavaScript. 

For JavaScript code, please see the [`time-js`](./time-js) module.

The versions `1.x` and early `2.x` snapshots are built using Java 8. 

Starting `2.0.0-SNAPSHOT.76`, all modules are built with Java 11. Therefore, consumer projects
should aim for Java 11+ to use them.
 
## Using Spine Time in a Gradle project

To add a dependency to a Gradle project, please use the following:

```kotlin
dependencies {
    implementation("io.spine:spine-time:$spineVersion") 
}
```

In addition to the generated types and basic factory and calculation routines, the library 
provides converters between its types and Java Time. It is expected that an application code would 
perform the date/time calculations using Java Time.
                                                   
## Integration with `kotlinx-datetime` 

Compatibility with [`kotlinx-datetime`][kotlinx-datetime] [planned][issue-113] for v2.0.

[travis]: https://travis-ci.com/SpineEventEngine/time
[travis-badge]: https://travis-ci.com/SpineEventEngine/time.svg?branch=master
[codecov]: https://codecov.io/gh/SpineEventEngine/time
[codecov-badge]: https://codecov.io/gh/SpineEventEngine/time/branch/master/graph/badge.svg
[license-badge]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat
[license]: http://www.apache.org/licenses/LICENSE-2.0

[java-time]: http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html                                                        
[kotlinx-datetime]: https://github.com/Kotlin/kotlinx-datetime
[issue-113]: https://github.com/SpineEventEngine/time/issues/113
