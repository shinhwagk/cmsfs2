name in ThisBuild := "cmsfs"

version in ThisBuild := "1.0"

scalaVersion in ThisBuild := "2.12.1"

val akkaVersion = "2.5.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion
)