package org.cmsfs.config.parser

sealed trait Environment
case class EnvironmentSsh(collectorId: Int, name: String) extends Environment

case class EnvironmentJdbc() extends Environment