package com.typesafe.sbt.duplicates

import sbt._

object DuplicatesFinderKeys {

  lazy val checkDuplicates = taskKey[Unit]("Check classpath for class or resources duplicates")
  lazy val excludePatterns = settingKey[Seq[String]]("Patterns to exclude when looking for duplicates")
  lazy val reportDuplicatesWithSameContent = settingKey[Boolean]("Report duplicates even file content is the same")
}
