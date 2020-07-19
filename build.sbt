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

libraryDependencies += {
  val utestVersion = scalaBinaryVersion.value match {
    case "2.10" | "2.11" => "0.6.8"
    case _ => "0.7.4"
  }
  "com.lihaoyi" %% "utest" % utestVersion % Test
}
testFrameworks += new TestFramework("utest.runner.Framework")

enablePlugins(SbtPlugin)
scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++
    Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}
