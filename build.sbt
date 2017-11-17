name := "play-akkajobs"

version := "2.6.2-SNAPSHOT"

organization := "com.github.tuxBurner"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
   "com.typesafe.play" %% "play" % "2.6.7",
   "com.typesafe.play" %% "play-java" % "2.6.7",
   "com.google.inject" % "guice" % "4.1.0"
)

publishTo := {
   if(isSnapshot.value) {
      Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
   } else {
      Some(Resolver.file("Github Pages",  new File("../tuxBurner.github.io/repo")))
   }
}


