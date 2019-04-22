organization := "com.github.sbt"

sbtPlugin := true
crossSbtVersions := Vector("0.13.16", "1.0.3")

scalacOptions := Seq(
  "-encoding", "UTF-8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfuture"
)

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

scalafmtOnCompile := true
