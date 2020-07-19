organization in ThisBuild := "com.github.sbt.test"
scalaVersion in ThisBuild := "2.11.12"

val core = project
val differ = project.dependsOn(core)
val equal = project.dependsOn(core)
