# Spine Time: Protobuf-based Date/Time types

[![Build Status][travis-badge]][travis] &nbsp; 
[![codecov][codecov-badge]][codecov] &nbsp;
[![license][license-badge]][license]

In addition to `Timestamp` and `Duration` natively available from Protobuf, the Spine Time library 
provides a set of data types for describing date and time in a business model. 

The types provided by this library follow the conventions offered by [Java Time](http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html).

## Supported Languages

Currently, the library supports only Java, with JavaScript and Dart being on the priority list.
 
## Using Spine Time in a Java Project

To add a dependency to a Gradle project, please use the following:

```groovy
dependencies {
    compile "io.spine:spine-time:$spineVersion"
}
```

In addition to the generated types and basic factory and calculation routines, the library 
provides converters between its types and Java Time. It is expected that an application would 
perform the date/time calculations using Java Time.

[travis]: https://travis-ci.com/SpineEventEngine/time
[travis-badge]: https://travis-ci.com/SpineEventEngine/time.svg?branch=master
[codecov]: https://codecov.io/gh/SpineEventEngine/time
[codecov-badge]: https://codecov.io/gh/SpineEventEngine/time/branch/master/graph/badge.svg
[license-badge]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat
[license]: http://www.apache.org/licenses/LICENSE-2.0
