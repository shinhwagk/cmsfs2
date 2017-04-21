package org.cmsfs.servie.collect.script.local

import org.cmsfs.servie.collect.CollectWorkerMessage

object CollectScriptLocalMessages {

  case class WorkerJob(name: String, path: Seq[String], args: Option[String], format: String) extends CollectWorkerMessage

}