name in ThisBuild := "cmsfs"

version in ThisBuild := "1.0"

scalaVersion in ThisBuild := "2.12.2"

val akkaVersion = "2.5.0"

val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion

val commonIO = "commons-io" % "commons-io" % "2.5"
val playJson = "com.typesafe.play" %% "play-json" % "2.6.0-M6"
val quartz = "org.quartz-scheduler" % "quartz" % "2.3.0"
val slick = "com.typesafe.slick" %% "slick" % "3.2.0"
val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
val mysqlJdbc = "mysql" % "mysql-connector-java" % "6.0.6"
val oracleJdbc = "com.oracle" % "jdbc" % "8"
val jsch = "com.jcraft" % "jsch" % "0.1.54"
val ws = "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.0-M6"

libraryDependencies ++= Seq(
  akkaCluster, akkaSlf4j, commonIO, playJson, quartz, slick, logback, mysqlJdbc, jsch, ws,
  oracleJdbc from "file:///" + baseDirectory.value / "lib" / "ojdbc8.jar"
)

enablePlugins(JavaAppPackaging)