package com.typesafe.sbt.duplicates

import scala.collection.immutable.Iterable

import sbt.File

case class Classpath(classpath: Seq[File], excludePatterns: Seq[String]) {

  private val classpathElements = classpath.collect { case f if f.exists() => ClasspathElement(f) }
  private val allClassesChecksums = sourcesAndChecksumsByName(classpathElements.map(_.classesChecksums))
  private val allResourcesChecksums = sourcesAndChecksumsByName(classpathElements.map(_.resourcesChecksums))
  val classesDuplicates = findDuplicates(allClassesChecksums).toList
  val resourcesDuplicates = findDuplicates(allResourcesChecksums).toList

  private def sourcesAndChecksumsByName(classpathElements: Seq[Map[String, Checksum]]) = {
    classpathElements.foldLeft(Map.empty[String, List[Checksum]]) {
      case (map, checksums) =>
        checksums.foldLeft(map) {
          case (m, (name, checksum)) =>
            val previousChecksums = if (m.contains(name)) m(name) else Nil
            m.updated(name, checksum :: previousChecksums)
        }
    }
  }

  private def findDuplicates(allChecksums: Map[String, List[Checksum]]): Iterable[Conflict] =
    allChecksums
      .filter { case (name, checksums) => checksums.size >= 2 && !excludePatterns.exists(r => name.matches(r)) }
      .flatMap {
        case (name, checksums) =>
          checksums.combinations(2).map { c =>
            val List(c1, c2) = c
            val state = if (c1 == c2) ConflictState.ContentEqual else ConflictState.ContentDiffer
            Conflict(name, c.map(_.source), state)
          }
      }.toList

}
