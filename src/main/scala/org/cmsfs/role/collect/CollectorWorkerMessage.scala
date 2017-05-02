package org.cmsfs.role.collect

import org.cmsfs.config.db.table.ConfTaskSchema
import org.cmsfs.role.collect.Collector.{CollectConfig, CollectorEnv}

object CollectorWorkerMessage {

  case class WorkerJob(confTaskSchema:ConfTaskSchema, collectConfig: CollectConfig, env: CollectorEnv)

}
