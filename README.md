# Spine Time: Protobuf-based Date/Time types

[![Build Status][travis-badge]][travis] &nbsp; 
[![codecov][codecov-badge]][codecov] &nbsp;
[![license][license-badge]][license]

In addition to `Timestamp` and `Duration` natively available from Protobuf, the Spine Time library 
provides a set of data types for describing date and time in a business model. 

The types provided by this library follow the conventions offered by [Java Time](http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html).

# This is 2.x `master` branch!

This branch contains the changes which aren't meant to be merged to `master` 
at least until the Spine 2.0.0 version release.

The code in this branch is **not production-ready**. Please use the released versions of 
Spine 1.x instead. The API modifications are significant. Some features aren't backward-compatible
with the 1.x functionality.

The codebase built on top of the `time` module in this branch is designed for **JDK 8 runtime**.

This branch must be treated as `master` for 2.x features, so the changes to it must go
through the PR review process.
 
## Versioning in 2.x branch

While preparing the release of the version 2.0.0 of the framework, the libraries are versioned 
as follows:

* `2.0.0-jdk8.SNAPSHOT.N` — a version of the libraires in-development; `N` is a positive integer 
which is incremented upon each changeset/PR;
* `2.0.0-jdk8.rc1`, `2.0.0-jdk8.rc2` etc  — the versions of the preliminary releases; 
each of these may be followed up by more `SNAPSHOT`s;
* `2.0.0-jdk8` — the first release of the Spine v2 for JDK 8 runtime.    

## Supported Languages

Currently the library supports only Java, with JavaScript and Dart being on the priority list.
 
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
