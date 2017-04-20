package org.cmsfs.servie.collect.jdbc

import org.cmsfs.servie.collect.CollectWorkerMessage

object CollectJdbcMessages {
  case class WorkerJob(name: String) extends CollectWorkerMessage
}
