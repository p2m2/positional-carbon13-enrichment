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
  .aggregate(positionalCarbonSources.js, positionalCarbonSources.jvm)
  .settings(
    name := "positional-carbon13-enrichment",
    version := "0.1.0-SNAPSHOT",
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
    coverageMinimumStmtTotal := 70,
    coverageMinimumBranchTotal := 30,
    coverageMinimumStmtPerPackage := 70,
    coverageMinimumBranchPerPackage := 30,
    coverageMinimumStmtPerFile := 70,
    coverageMinimumBranchPerFile := 30,
    coverageFailOnMinimum := true,
    coverageHighlighting := true
  )

// Project containing source code shared between the JS and JVM projects.
// This project should never be compiled or packages but is simply an IntelliJ IDEA
// friendly alternative to a shared code directory. Projects depending on this
// projects source code should declare a dependency as 'Provided' AND append
// this projects source directory manually to 'unmanagedSourceDirectories'.
lazy val PositionalCarbon13EnrichmentShared = project.in(file("shared"))

lazy val PositionalCarbon13EnrichmentSharedSettings = Seq(
  name := "foo",
  version := "0.1-SNAPSHOT",
  // NOTE: The following line will generate a warning in IntelliJ IDEA, which can be ignored:
  // "The following source roots are outside the corresponding base directories"
  Compile / unmanagedSourceDirectories += ( (PositionalCarbon13EnrichmentShared / Compile) / scalaSource).value
)


lazy val positionalCarbonSources = crossProject(JSPlatform, JVMPlatform).in(file(".")).
  settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "utest" % "0.8.1" % Test
    ),
    testFrameworks += new TestFramework("utest.runner.Framework")
  ).
  jvmSettings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "utest" % "0.8.1" % Test,
      "com.github.scopt" %% "scopt" % "4.1.0"
    ),

   // Compile / unmanagedSourceDirectories += baseDirectory.value.getParentFile.getParentFile / "shared"/"src"/"main"/"scala",
   // Test / unmanagedSourceDirectories += baseDirectory.value.getParentFile.getParentFile / "shared"/"src"/"test"/"scala",

    Compile / mainClass := Some("fr.inrae.p2m2.app.PositionalCarbon13EnrichmentMain") ,
    assembly / target := file("assembly"),
    assembly / assemblyJarName := s"${name.value}-${version.value}.jar"
  )
  .enablePlugins(BuildInfoPlugin)
  .jsSettings(
    // Add JS-specific settings here
    scalaJSUseMainModuleInitializer := true,
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "utest" % "0.8.1" % Test,
      "org.scala-js" %%% "scalajs-dom" % "2.1.0",
      "com.lihaoyi" %%% "scalatags" % "0.12.0"
    )
  )

Global / onChangedBuildSource := ReloadOnSourceChanges
