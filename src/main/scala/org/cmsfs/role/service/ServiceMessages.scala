package org.cmsfs.role.service

import org.cmsfs.config.db.table.ConfTaskService

object ServiceMessages {

  case class WorkerJob(confService: ConfTaskService, env: Map[String, String], data: String)

}
