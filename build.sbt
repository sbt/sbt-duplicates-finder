organization := "com.github.sbt"

enablePlugins(SbtPlugin)

scalacOptions := Seq(
  "-encoding",
  "UTF-8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfuture"
)

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

scalafmtOnCompile := true

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "utest" % "0.7.10" % Test
)

testFrameworks += new TestFramework("utest.runner.Framework")

scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++
    Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}
