package org.cmsfs.servie.process

import org.cmsfs.config.db.table.ConfTaskProcess
import org.cmsfs.servie.collect.Collector.CollectorEnv

object ProcessMessages {

  case class WorkerJob(confTaskProcess: ConfTaskProcess, result: String, env: CollectorEnv)

}
