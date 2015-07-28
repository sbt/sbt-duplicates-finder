package com.typesafe.sbt.duplicates

import java.io.File

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
    excludePatterns := List("^META-INF/.*", "reference.conf"),
    reportDuplicatesWithSameContent := false,
    includeBootClasspath := false,
    checkDuplicates <<= checkDuplicates0
  )

  private lazy val checkDuplicates0 = Def.task {
    val log = streams.value.log
    val reportSameContent = reportDuplicatesWithSameContent.value
    val additionalClasspath = if(includeBootClasspath.value) bootClasspath else Seq.empty
    val classpath = Classpath(fullClasspath.value.files ++ additionalClasspath, excludePatterns.value)
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

  private def bootClasspath: Seq[File] =
    sys.props("sun.boot.class.path").split(File.pathSeparator).map(new File(_)).filter(_.exists())
}
