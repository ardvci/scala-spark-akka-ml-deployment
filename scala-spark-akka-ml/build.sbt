import scala.collection.immutable.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "scala-spark-akka-ml"
  )

libraryDependencies += "org.apache.spark" %% "spark-core" % "3.2.3"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "3.2.3"

val akkaVersion = "2.7.0"
val akkaHttpVersion = "10.5.2"

libraryDependencies ++= Seq(
  // akka streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  // akka http
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
)