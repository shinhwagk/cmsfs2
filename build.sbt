name in ThisBuild := "cmsfs"

version in ThisBuild := "1.0"

scalaVersion in ThisBuild := "2.12.1"

val akkaVersion = "2.5.0"

val commonIO = "commons-io" % "commons-io" % "2.5"
val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
val playJson = "com.typesafe.play" % "play-json_2.12" % "2.6.0-M6"
val quartz = "org.quartz-scheduler" % "quartz" % "2.2.3"

libraryDependencies ++= Seq(
  akkaCluster, commonIO, playJson, quartz
)

enablePlugins(JavaAppPackaging)