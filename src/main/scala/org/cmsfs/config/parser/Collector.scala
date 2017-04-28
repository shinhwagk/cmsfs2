package org.cmsfs.config.parser

sealed trait Collector {

  val path: Seq[String]

  val args: Option[String]
}

case class CollectorSsh(override val path: Seq[String], override val args: Option[String]) extends Collector

case class CollectorJdbc(override val path: Seq[String], override val args: Option[String]) extends Collector

case class CollectorLocal(override val path: Seq[String], override val args: Option[String]) extends Collector