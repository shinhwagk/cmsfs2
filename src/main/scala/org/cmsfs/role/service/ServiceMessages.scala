package org.cmsfs.role.service

object ServiceMessages {

  case class WorkerJob(method: String, url: String, body: Option[String])

}
