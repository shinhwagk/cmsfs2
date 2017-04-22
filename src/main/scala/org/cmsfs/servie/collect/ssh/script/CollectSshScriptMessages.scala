package org.cmsfs.servie.collect.ssh.script

import org.cmsfs.servie.collect.CollectWorkerMessage

object CollectSshScriptMessages {

  case class Connector(name: String, ip: String, port: Int, username: String, password: Option[String], privateKey: Option[String])

  case class Collector(name: String, path: String, args: Option[String])

  case class WorkerJob(name: String, connect: Connector, collect: Collector, next: String) extends CollectWorkerMessage
}
