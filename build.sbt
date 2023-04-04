ThisBuild / scalaVersion := "2.13.10"
ThisBuild / organization := "com.github.p2m2"
ThisBuild / organizationName := "p2m2"
ThisBuild / organizationHomepage := Some(url("https://www6.inrae.fr/p2m2"))
ThisBuild / licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/p2m2/positional-carbon13-enrichment"),
    "scm:git@github.com:p2m2/positional-carbon13-enrichment.git"
  )
)

ThisBuild / developers := List(
  Developer("ofilangi", "Olivier Filangi", "olivier.filangi@inrae.fr",url("https://github.com/ofilangi"))
)

lazy val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "positional-carbon13-enrichment",
    version := "1.0.0",
    credentials += {

      val realm = scala.util.Properties.envOrElse("REALM_CREDENTIAL", "")
      val host = scala.util.Properties.envOrElse("HOST_CREDENTIAL", "")
      val login = scala.util.Properties.envOrElse("LOGIN_CREDENTIAL", "")
      val pass = scala.util.Properties.envOrElse("PASSWORD_CREDENTIAL", "")

      val file_credential = Path.userHome / ".sbt" / ".credentials"

      if (reflect.io.File(file_credential).exists) {
        Credentials(file_credential)
      } else {
        Credentials(realm, host, login, pass)
      }
    },
    publishTo := {
      if (isSnapshot.value)
        Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/content/repositories/snapshots")
      else
        Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
    },
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
    pomIncludeRepository := { _ => false },
    publishMavenStyle := true,
    coverageMinimumStmtTotal := 0,
    coverageMinimumBranchTotal := 0,
    coverageMinimumStmtPerPackage := 0,
    coverageMinimumBranchPerPackage := 0,
    coverageMinimumStmtPerFile := 0,
    coverageMinimumBranchPerFile := 0,
    coverageFailOnMinimum := false,
    coverageHighlighting := true,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "utest" % "0.8.1" % Test,
      "org.scala-js" %%% "scalajs-dom" % "2.1.0" % Test,
      "com.lihaoyi" %%% "scalatags" % "0.12.0",
    ),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    assembly / target := file("assembly"),
    assembly / assemblyJarName := s"${name.value}-${version.value}.jar",
    Compile / fastOptJS / scalaJSLinkerConfig ~= {
      _.withOptimizer(false)
        .withPrettyPrint(true)
        .withSourceMap(true)
    },
    Compile / fullOptJS / scalaJSLinkerConfig ~= {
      _.withSourceMap(false)
    },
    Compile / scalaJSUseMainModuleInitializer := true,
    Test / jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
  )

Global / onChangedBuildSource := ReloadOnSourceChanges
