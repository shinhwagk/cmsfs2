package org.cmsfs.servie.terminal

import org.cmsfs.config.db.table.ConfTaskTerminal
import org.cmsfs.servie.collect.Collector.CollectorEnv

object TerminalMessages {

  case class WorkerJob(confTaskService: ConfTaskTerminal, processResult: String, env: CollectorEnv)

}
