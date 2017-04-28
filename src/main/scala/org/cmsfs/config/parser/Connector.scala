package org.cmsfs.config.parser

sealed trait Connector

case class ConnectorSsh(ip: String, port: Int, username: String, password: Option[String], privateKey: Option[String]) extends Connector

case class ConnectorJdbc(ip: String, port: Int, service: String, username: String, password: String) extends Connector