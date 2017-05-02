package org.cmsfs.role.collect

import org.cmsfs.config.db.table.ConfTaskSchema

object CollectorServiceMessage {

  case class WorkerJob(task: ConfTaskSchema, utcDate: String)

}
