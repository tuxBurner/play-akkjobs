name := "play-akkajobs"

version := "1.0.1-SNAPSHOT"

organization := "com.github.tuxBurner"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.4", "2.11.7")

libraryDependencies ++= Seq(
   "com.typesafe.play" %% "play" % "2.5.12",
   "com.typesafe.play" %% "play-java" % "2.5.12"
)

publishTo <<= version {
  case v if v.trim.endsWith("SNAPSHOT") => Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
  case _ => Some(Resolver.file("Github Pages",  new File("../tuxBurner.github.io/repo")))
}
