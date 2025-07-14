
lazy val root = (project in file("."))
  .settings(
    name := "play-akkajobs",

    version := "2.9.0-SNAPSHOT",

    organization := "com.github.tuxBurner",

    resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/",

    scalaVersion := "2.13.15",

    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % "2.9.6",
      "com.typesafe.play" %% "play-java" % "2.9.6",
      "com.google.inject" % "guice" % "6.0.0",
      "org.apache.commons" % "commons-lang3" % "3.17.0",
      "org.reflections" % "reflections" % "0.10.2" // TODO: get rid of this
    ),

    publishTo := {
      if (isSnapshot.value) {
        Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/.m2/repository")))
      } else {
        Some(Resolver.file("Github Pages", new File("../tuxBurner.github.io/repo")))
      }
    }
  )

