package com.github.sbt.duplicates

import sbt._

import scala.collection.JavaConverters._

object ClasspathElement {

  def apply(base: File): ClasspathElement =
    if (base.isDirectory) buildFromDirectory(base) else buildFromJar(base)

  private def apply(base: File, entries: Seq[ClasspathEntity]): ClasspathElement = {
    val (classes, resources) = entries.partition(_.name.endsWith(".class"))
    ClasspathElement(base, classes, resources)
  }

  private def buildFromDirectory(base: File): ClasspathElement =
    ClasspathElement(
      base,
      base.**(-DirectoryFilter).pair(Compat.relativeTo(base)).map(_._2).map(new FileEntity(base, _))
    )

  private def buildFromJar(base: File): ClasspathElement =
    ClasspathElement(
      base,
      Compat.Using.zipFile(base) { zip =>
        zip.entries().asScala.filterNot(_.isDirectory).map(e => new ZipEntity(base, e.getName)).toList
      }
    )
}

case class ClasspathElement(source: File, classes: Seq[ClasspathEntity], resources: Seq[ClasspathEntity]) {
  def classesChecksums: Map[String, ClasspathEntity]   = byPath(classes)
  def resourcesChecksums: Map[String, ClasspathEntity] = byPath(resources)

  private def byPath(entries: Seq[ClasspathEntity]): Map[String, ClasspathEntity] =
    entries.map(entry => entry.name -> entry).toMap
}
