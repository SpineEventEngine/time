# Spine Time: Protobuf-based Date/Time types

[![Build Status](https://travis-ci.org/SpineEventEngine/time.svg?branch=master)](https://travis-ci.org/SpineEventEngine/time)
[![codecov](https://codecov.io/gh/SpineEventEngine/time/branch/master/graph/badge.svg)](https://codecov.io/gh/SpineEventEngine/time)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6c8b20d9a39149708b6a607615c8b1be)](https://www.codacy.com/app/SpineEventEngine/time?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=SpineEventEngine/time&amp;utm_campaign=Badge_Grade)
[![license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

In addition to `Timestamp` and `Duration` natively available from Protobuf, the Spine Time library 
provides a set of data types for describing date and time in a business model. 

The types provided by  this library follow the conventions offered by [Java Time](http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html).

## Supported Languages

Currently the library supports only Java, with JavaScript and C++ being on the priority list.

 
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
