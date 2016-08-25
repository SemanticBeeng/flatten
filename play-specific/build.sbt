//import CommonSettings._
//import Dependencies._

name := "flatten-play"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//libraryDependencies ++= Seq(
//  "org.scalaz" %% "scalaz-core" % "7.2.5",
//  // Typeclass instances for Future. Not necessary for Scalaz 7.1.
//  "org.typelevel" %% "scalaz-contrib-210" % "0.1.5"
//  )
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.5"

//Project("play-specific", file("play-specific"))
//  .settings(projectSettings:_*)
//  .enablePlugins(play.sbt.Play)

  //PlayProject("play-specific")
