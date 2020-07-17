package com.github.sbt.duplicates

import com.github.sbt.duplicates.ConflictState._
import sbt._
import utest._
import TestCompat._

object ClasspathTests extends TestSuite {

  val dir = TestCompat.SbtIO.createTemporaryDirectory
  dir.deleteOnExit()
  val scalaLibraryJar = dir / "libs" / "scala-library.jar"
  val classes         = dir / "classes"

  // need a class and jar. the scala library will do.
  val noneClass = None.getClass
  SbtIO.transfer(noneClass.getProtectionDomain.getCodeSource.getLocation.openStream(), scalaLibraryJar)
  SbtIO.transfer(noneClass.getClassLoader.getResourceAsStream("scala/None$.class"), classes / "scala" / "None$.class")
  SbtIO.write(classes / "scala" / "Some.class", "💥 CONFLICT WITH SCALA-LIBRARY!!!".getBytes)
  SbtIO.write(classes / "META-INF" / "MANIFEST.MF", "💥 CONFLICT WITH SCALA-LIBRARY!!!")

  override val tests = utest.Tests {
    "find duplicates" - {
      val cp      = Classpath(classpath = Seq(scalaLibraryJar, classes), excludePatterns = Nil)
      val dupeMap = cp.classesDuplicates.groupBy(_.name)
      assert(
        dupeMap("scala/None$.class") == List(
          Conflict("scala/None$.class", List(classes, scalaLibraryJar), ContentEqual)
        )
      )
      assert(
        dupeMap("scala/Some.class") == List(Conflict("scala/Some.class", List(classes, scalaLibraryJar), ContentDiffer))
      )
      dupeMap.size ==> 2

      cp.resourcesDuplicates ==> List(Conflict("META-INF/MANIFEST.MF", List(classes, scalaLibraryJar), ContentDiffer))
    }
  }
}
