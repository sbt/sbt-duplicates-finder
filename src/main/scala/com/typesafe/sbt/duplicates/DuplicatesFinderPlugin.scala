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
    reportDuplicates := reportDuplicates0.value,
    checkDuplicates := checkDuplicates0.value
  )
  // Borrowed from https://github.com/jrudolph/sbt-dependency-graph/master/src/main/scala/net/virtualvoid/sbt/graph/DependencyGraphSettings.scala
  // This is to support 0.13.8's InlineConfigurationWithExcludes while not forcing 0.13.8
  type HasModule = {
    val module: ModuleID
  }

  def crossName(ivyModule: IvySbt#Module) =
    ivyModule.moduleSettings match {
      case ic: InlineConfiguration ⇒ ic.module.name
      case hm: HasModule if hm.getClass.getName == "sbt.InlineConfigurationWithExcludes" ⇒ hm.module.name
      case _ ⇒
        throw new IllegalStateException("sbt-duplicates-finder plugin currently only supports InlineConfiguration of ivy settings (the default in sbt)")
    }

  private lazy val reportFileName = Def.task {
    val crossTarget = Keys.crossTarget.value
    val projectID = Keys.projectID.value
    val ivyModule = Keys.ivyModule.value
    val configuration = Keys.configuration.value.name
    val org = projectID.organization
    val name = crossName(ivyModule)
    file(s"$crossTarget/resolution-cache/reports/$org-$name-$configuration.duplicates.log")
  }

  private lazy val reportDuplicates0 = Def.task {
    val Seq(classConflicts, resourceConflicts) = findDuplicates.value
    val logLines = createLogLines(classConflicts, "class") ++ createLogLines(resourceConflicts, "resource")
    if (logLines.nonEmpty) {
      val outputFile = reportFileName.value
      IO.writeLines(outputFile, logLines)
      streams.value.log.info(s"*** sbt-duplicates-finder: report written to ${outputFile.getCanonicalPath}")
      Some(outputFile)
    } else None
  }

  private lazy val findDuplicates = Def.task {
    val reportIfSameContent = reportDuplicatesWithSameContent.value
    val additionalClasspath = if (includeBootClasspath.value) bootClasspath else Seq.empty
    val classpath = Classpath(fullClasspath.value.files ++ additionalClasspath, excludePatterns.value)

    Seq(classpath.classesDuplicates, classpath.resourcesDuplicates)
      .map(_.filter(conflict ⇒ !(reportIfSameContent && conflict.conflictState == ConflictState.ContentEqual))
      )
  }

  private def createLogLines(duplicates: List[Conflict], name: String): Seq[String] = {
    val count = duplicates.length
    if (count > 0) {
      Seq(s"Detected $count $name conflicts:") ++ duplicates.flatMap { conflict ⇒
        Seq("", s"- ${conflict.name}: ${conflict.conflictState}") ++
          conflict.conflicts.map(file ⇒ s"\t - $file")
      }
    } else
      Seq[String]()
  }

  private lazy val checkDuplicates0 = Def.task {
    val log = streams.value.log
    val Seq(classConflicts, resourceConflicts) = findDuplicates.value
    logDuplicates(classConflicts, log, "classes")
    logDuplicates(resourceConflicts, log, "resources")
  }

  private def logDuplicates(duplicates: List[Conflict], log: Logger, name: String) = createLogLines(duplicates, name).foreach(l ⇒ log.warn(l))

  private def bootClasspath: Seq[File] =
    sys.props("sun.boot.class.path").split(File.pathSeparator).map(new File(_))
}
