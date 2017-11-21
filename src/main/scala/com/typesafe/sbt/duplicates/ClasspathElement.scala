package com.typesafe.sbt.duplicates

import java.security.MessageDigest

import scala.collection.JavaConverters._
import sbt._

object ClasspathElement {

  def apply(base: File): ClasspathElement =
    if (base.isDirectory) buildFromDirectory(base) else buildFromJar(base)

  private def apply(base: File, classes: Seq[String], resources: Seq[String]): ClasspathElement =
    ClasspathElement(base, classes.map(c => computeSha(base, c)).toMap, resources.map(r => computeSha(base, r)).toMap)

  private def buildFromDirectory(base: File): ClasspathElement = {
    val (classes, resources) = base.**(-DirectoryFilter).pair(Compat.relativeTo(base)).map(_._2).partition(_.endsWith(".class"))
    ClasspathElement(base, classes, resources)
  }

  private def buildFromJar(base: File): ClasspathElement =
    Compat.Using.zipFile(base) { zip =>
      val (classes, resources) = zip.entries().asScala.filterNot(_.isDirectory).map(_.getName).toList.partition(_.endsWith(".class"))
      ClasspathElement(base, classes, resources)
    }

  private def computeSha(base: File, path: String): (String, String) = {
    def sha(bytes: Array[Byte]): String = {
      val md = MessageDigest.getInstance("SHA-256")
      val digestBytes = md.digest(bytes)
      digestBytes.map(byte => f"$byte%02X").foldLeft(StringBuilder.newBuilder)(_ append _).mkString
    }

    if (base.isDirectory)
      path -> sha(IO.readBytes(base / path))
    else
      Compat.Using.zipFile(base) { zipFile =>
        Option(zipFile.getEntry(path))
          .map(zipFile.getInputStream)
          .map(IO.readBytes)
          .map(bytes => path -> sha(bytes))
          .getOrElse(throw new IllegalStateException(s"Could not get $path from $base"))
      }

  }
}
case class ClasspathElement(source: File, classes: Map[String, String], resources: Map[String, String]) {
  def classesChecksums: Map[String, Checksum] = checksums(classes)
  def resourcesChecksums: Map[String, Checksum] = checksums(resources)

  private def checksums(map: Map[String, String]): Map[String, Checksum] =
    map.map { case (k, v) => k -> Checksum(source, v) }
}
