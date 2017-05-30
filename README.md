# sbt-duplicates-finder

The `sbt-duplicates-finder` plugin offers utilities to help you find classes or resources that could create conflicts in your classpath.

## Adding to your project

Add the following line to your `project/plugins.sbt`:

```
addSbtPlugin("org.scala-sbt" % "sbt-duplicates-finder" % "0.7.0")
```

Since `sbt-duplicates-finder` is an AutoPlugin, it will be automatically available to your projects.

## Usage

```
> checkDuplicates
```

List all resources and classes conflicts found (searching in the project's dependencies and own classes).

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
