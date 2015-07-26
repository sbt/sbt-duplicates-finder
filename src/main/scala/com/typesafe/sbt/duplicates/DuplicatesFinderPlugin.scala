package com.typesafe.sbt.duplicates

import sbt._
import sbt.Keys._

object DuplicatesFinderPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements
  override def projectSettings =
    Seq(Compile, Test, Runtime).flatMap(inConfig(_)(baseSettings))

  val autoImport = DuplicatesFinderKeys

  import autoImport._

  lazy val baseSettings = Seq(
    excludePatterns := List("^META-INF/.*"),
    reportDuplicatesWithSameContent := false,
    checkDuplicates <<= checkDuplicates0
  )

  private lazy val checkDuplicates0 = Def.task {
    val log = streams.value.log
    val reportSameContent = reportDuplicatesWithSameContent.value
    val classpath = Classpath(fullClasspath.value.files, excludePatterns.value)
    logDuplicates(classpath.classesDuplicates, log, "classes", reportSameContent)
    logDuplicates(classpath.resourcesDuplicates, log, "resources", reportSameContent)
  }

  private def logDuplicates(duplicates: List[Conflict], log: Logger, name: String, reportIfSameContent: Boolean) =
    if(duplicates.nonEmpty) {
      log.warn(s"Detected $name conflicts:")
      duplicates.foreach { conflict =>
        if(!(reportIfSameContent && conflict.conflictState == ConflictState.ContentEqual)) {
          log.warn("")
          log.warn(s"- ${conflict.name}: ${conflict.conflictState}")
          conflict.conflicts.foreach { file =>
            log.warn(s"\t - $file")
          }
        }
      }
    }
}
