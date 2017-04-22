package org.cmsfs.servie.collect.jdbc

import org.cmsfs.servie.collect.CollectWorkerMessage

object CollectJdbcMessages {

  case class Connector(name: String, jdbcUrl: String, user: String, password: String)

  case class Collector(name: String, path: String, args: Option[String])

  case class WorkerJob(name: String, connect: Connector, collect: Collector, next: String) extends CollectWorkerMessage

}
