package org.cmsfs.role.trigger.collect

import org.cmsfs.config.db.table.ConfTaskSchema
import org.cmsfs.role.trigger.collect.Collector.CollectConfig

object CollectorWorkerMessage {

  case class WorkerJob(confTaskSchema: ConfTaskSchema, collectConfig: CollectConfig, env: Map[String, String])

}
