package com.github.sbt.duplicates

import sbt._

object DuplicatesFinderKeys {

  lazy val checkDuplicates = taskKey[Unit]("Check classpath for class or resource duplicates")
  lazy val reportDuplicates =
    taskKey[Option[File]]("Print classpath for class or resource duplicates to a file, if there are any conflicts")
  lazy val checkDuplicatesTest             = taskKey[Unit]("Fail the build if there are class or resource duplicates")
  lazy val excludePatterns                 = settingKey[Seq[String]]("Patterns to exclude when looking for duplicates")
  lazy val reportDuplicatesWithSameContent = settingKey[Boolean]("Report duplicates even file content is the same")
  lazy val includeBootClasspath =
    settingKey[Boolean]("Include the boot classpath to the list of sources scanned for duplicates")
}
