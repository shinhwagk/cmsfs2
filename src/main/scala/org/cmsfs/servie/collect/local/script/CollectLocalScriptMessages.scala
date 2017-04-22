package org.cmsfs.servie.collect.local.script

import org.cmsfs.servie.collect.CollectWorkerMessage

object CollectLocalScriptMessages {

  case class Collect(name: String, path: String, args: Option[String])

  case class WorkerJob(name: String, collect: Collect, next: String) extends CollectWorkerMessage

}