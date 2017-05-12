package org.cmsfs.role.collect

import akka.actor.{Actor, ActorLogging}
import akka.routing.FromConfig
import org.cmsfs.config.db.table.ConfTaskAction
import org.cmsfs.role.process.ProcessMessages

abstract class CollectorWorkerCore extends Actor with ActorLogging {

//  val actionActor = context.actorOf(FromConfig.props(), "action")
//
//  def toProcess(result: String, processes: Seq[ConfTaskAction], env: Map[String, String]): Unit = {
//    processes.foreach { process =>
//      actionActor ! ProcessMessages.WorkerJob(process, result, env, process.args)
//    }
//  }
//
//  def toProcess(resultOpt: Option[String], processesOpt: Option[Seq[ConfTaskAction]])(implicit env: Map[String, String]): Unit = {
//    if (resultOpt.isEmpty) {
//      log.warning(s"collect ${env.get("collect-name")} not result.")
//    } else if (processesOpt.isEmpty) {
//      log.warning(s"collect ${env.get("collect-name")} not processes.")
//    } else {
//      toProcess(resultOpt.get, processesOpt.get, env)
//    }
//  }
}
