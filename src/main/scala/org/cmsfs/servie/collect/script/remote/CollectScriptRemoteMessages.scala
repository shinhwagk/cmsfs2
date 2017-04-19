package org.cmsfs.servie.collect.script.remote

import org.cmsfs.servie.collect.CollectWorkerMessage

object CollectScriptRemoteMessages {

  case class WorkerJob(name: String) extends CollectWorkerMessage

}
