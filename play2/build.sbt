lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += "io.cettia" % "cettia-server" % "1.2.0"
libraryDependencies += "io.cettia.asity" % "asity-bridge-play2" % "3.0.0-Beta1"
libraryDependencies += "io.cettia.starter" % "example-server" % "0.1.0-SNAPSHOT"

resolvers += Resolver.mavenLocal

PlayKeys.devSettings := Seq("play.server.http.port" -> "8080")