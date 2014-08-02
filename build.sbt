name := "sdynamic"

organization := "xrrocha"

version := "1.0"

scalaVersion := "2.11.2"

scalacOptions ++= Seq(
  "-language:dynamics",
  "-deprecation",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-language:experimental.macros")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases")
)

libraryDependencies ++= Seq(
  "org.yaml" % "snakeyaml" % "1.13",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2",
  "org.scala-lang" % "scala-compiler" % "2.11.2",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "org.slf4j" % "slf4j-log4j12" % "1.7.7",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.1.2" % "test"
)


