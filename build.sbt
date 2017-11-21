organization := "org.scala-sbt"

sbtPlugin := true
crossSbtVersions := Vector("0.13.16", "1.0.3")

scalacOptions := Seq(
  "-encoding", "UTF-8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfuture"
)
