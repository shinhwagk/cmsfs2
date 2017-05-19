package org.cmsfs.role.collect

import akka.actor.{Actor, ActorLogging, ActorRef}
import org.cmsfs.config.db.table.ConfTaskAction
import org.cmsfs.role.action.ActionMessages

abstract class CollectorWorkerCore(collectorMasterActorRef: ActorRef) extends Actor with ActorLogging {

  def toProcess(result: String, processes: Seq[ConfTaskAction], env: Map[String, String]): Unit = {
    processes.foreach { action =>
      log.info(s"collect result: ${result.substring(10)}")
      log.info("send collect result to action start.")
      collectorMasterActorRef ! ActionMessages.WorkerJob(result, action, env)
      log.info("send collect result to action end.")
    }
  }

  def toProcess(resultOpt: Option[String], processesOpt: Option[Seq[ConfTaskAction]], env: Map[String, String]): Unit = {
    if (resultOpt.isEmpty) {
      log.warning(s"collect ${env.get("collect-name")} not result.")
    } else if (processesOpt.isEmpty) {
      log.warning(s"collect ${env.get("collect-name")} not processes.")
    } else {
      toProcess(resultOpt.get, processesOpt.get, env)
    }
  }
}
