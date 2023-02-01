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
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "positional-carbon13-enrichment",
    version := "0.1.0-SNAPSHOT",
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    //buildInfoPackage := "fr.inrae.p2m2.build",
   // idePackagePrefix := Some("fr.inrae.p2m2.tools"),
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "4.1.0",
      "com.lihaoyi" %% "utest" % "0.8.1" % Test,
    ),
    publishTo := {
      if (isSnapshot.value)
        Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/content/repositories/snapshots")
      else
        Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
    },
    scalaJSUseMainModuleInitializer := true,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
    pomIncludeRepository := { _ => false },
    publishMavenStyle := true,
    coverageMinimumStmtTotal := 70,
    coverageMinimumBranchTotal := 30,
    coverageMinimumStmtPerPackage := 70,
    coverageMinimumBranchPerPackage := 30,
    coverageMinimumStmtPerFile := 70,
    coverageMinimumBranchPerFile := 30,
    coverageFailOnMinimum := true,
    coverageHighlighting := true,
    testFrameworks += new TestFramework("utest.runner.Framework"),
    assembly / target := file("assembly"),
    assembly / assemblyJarName := s"${name.value}-${version.value}.jar",
  )
