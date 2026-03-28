name := """play-backend"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.18"

libraryDependencies ++= Seq(
  guice,
  javaJdbc,
  filters,
  "org.postgresql" % "postgresql" % "42.7.5",
  "org.flywaydb" % "flyway-core" % "9.22.3",
  "com.auth0" % "java-jwt" % "4.4.0",
  "org.mindrot" % "jbcrypt" % "0.4",
  "com.h2database" % "h2" % "2.3.232" % Test,
  "junit" % "junit" % "4.13.2" % Test
)

dependencyOverrides ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.14.3",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.14.3",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.3"
)
