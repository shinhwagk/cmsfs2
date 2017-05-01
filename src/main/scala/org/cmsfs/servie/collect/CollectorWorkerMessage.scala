package org.cmsfs.servie.collect

import org.cmsfs.config.db.table.ConfTaskSchema
import org.cmsfs.servie.collect.Collector.{CollectConfig, CollectorEnv}

object CollectorWorkerMessage {

  case class WorkerJob(confTaskSchema:ConfTaskSchema, collectConfig: CollectConfig, env: CollectorEnv)

}
