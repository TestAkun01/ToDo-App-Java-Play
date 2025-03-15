name := """todo-app-java-play"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  guice,
  javaJdbc,
  evolutions,
  "org.postgresql" % "postgresql" % "42.2.27"
)

