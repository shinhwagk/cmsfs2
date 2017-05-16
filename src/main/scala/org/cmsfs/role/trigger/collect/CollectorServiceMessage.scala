package org.cmsfs.role.trigger.collect

import org.cmsfs.config.db.table.ConfTaskSchema

object CollectorServiceMessage {

  case class WorkerJob(task: ConfTaskSchema, utcDate: String)

}
