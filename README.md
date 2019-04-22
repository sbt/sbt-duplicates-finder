# sbt-duplicates-finder

[![Build Status](https://travis-ci.org/sbt/sbt-duplicates-finder.svg?branch=master)](https://travis-ci.org/sbt/sbt-duplicates-finder)

The `sbt-duplicates-finder` plugin offers utilities to help you find classes or resources that could create conflicts in your classpath.


## Adding to your project

Add the following line to your `project/plugins.sbt`:

```
addSbtPlugin("com.github.sbt" % "sbt-duplicates-finder" % "1.0.0")
```

Since `sbt-duplicates-finder` is an AutoPlugin, it will be automatically available to your projects.

### 1.0.0 changes

With the 1.0.0 release, organizations are package hierarchy changed:
* Organization moved from `org.scala-sbt` to `com.github.sbt`
* Package moved from `com.typesafe.sbt.duplicates` to `com.github.sbt.duplicates`

## Usage

```
> checkDuplicates
```

List all resources and classes conflicts found (searching in the project's dependencies and own classes).

```
> checkDuplicatesTest
```

Same as `checkDuplicates`, but fails the build if duplicates classes or resources are found.

### Settings

#### `reportDuplicatesWithSameContent`

This setting, defaulting to `false`, controls whether classes/resources with the same name but also the
same content are reported.

#### `excludePatterns`

This setting, defaulting to `Seq(^META-INF/.*)`, allows to exclude filenames matching one of these patterns from the duplicates report.

#### `includeBootClasspath`

This setting defaulting to `false`, allows to include the boot classpath in the search for duplicates. This allows to detect classes that conflict with JDK classes.

# License

This software is under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
