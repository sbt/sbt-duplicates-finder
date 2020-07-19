package com.github.sbt.duplicates

import com.github.sbt.duplicates.ConflictState._
import sbt._
import sbt.io.IO
import utest._

object ClasspathTests extends TestSuite {

  val dir = IO.createTemporaryDirectory
  dir.deleteOnExit()
  val scalaLibraryJar = dir / "libs" / "scala-library.jar"
  val classes         = dir / "classes"

  // need a class and jar. the scala library will do.
  val noneClass = None.getClass
  IO.transfer(noneClass.getProtectionDomain.getCodeSource.getLocation.openStream(), scalaLibraryJar)
  IO.transfer(noneClass.getClassLoader.getResourceAsStream("scala/None$.class"), classes / "scala" / "None$.class")
  IO.write(classes / "scala" / "Some.class", "💥 CONFLICT WITH SCALA-LIBRARY!!!".getBytes)
  IO.write(classes / "META-INF" / "MANIFEST.MF", "💥 CONFLICT WITH SCALA-LIBRARY!!!")

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
