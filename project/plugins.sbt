addSbtPlugin("org.jetbrains.scala" % "sbt-ide-settings"             % "1.1.1")
addSbtPlugin("com.eed3si9n"       % "sbt-assembly"                  % "1.2.0")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"                 % "2.0.0")
addSbtPlugin("io.crashbox"        % "sbt-gpg"                       % "0.2.1")
addSbtPlugin("com.github.sbt"     % "sbt-release"                   % "1.1.0")
addSbtPlugin("com.eed3si9n"       % "sbt-buildinfo"                 % "0.11.0")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "1.13.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.0.0")

libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"