organization := "org.scala-sbt"

sbtPlugin := true

scalacOptions := Seq(
  "-encoding", "UTF-8",
  "-target:jvm-1.6",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfuture"
)