package org.cmsfs.servie.collect

import org.cmsfs.config.db.table.ConfTaskSchema

object CollectorServiceMessage {

  case class WorkerJob(task: ConfTaskSchema, utcDate: String)

}
