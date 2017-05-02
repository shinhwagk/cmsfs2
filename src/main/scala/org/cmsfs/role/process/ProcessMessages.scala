package org.cmsfs.role.process

import org.cmsfs.config.db.table.ConfTaskProcess
import org.cmsfs.role.collect.Collector.CollectorEnv

object ProcessMessages {

  case class WorkerJob(confTaskProcess: ConfTaskProcess, result: String, env: CollectorEnv)

}
