package org.cmsfs.role.collect

import akka.actor.{Actor, ActorLogging, RootActorPath}
import akka.cluster.Member
import org.cmsfs.ClusterInfo.Actor_Process
import org.cmsfs.config.db.table.ConfTaskAction
import org.cmsfs.role.process.ProcessMessages

import scala.collection.mutable
import scala.util.Random

abstract class CollectorWorkerCore(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) extends Actor with ActorLogging {

  def toProcess(result: String, processes: Seq[ConfTaskAction], env: Map[String, String]): Unit = {
    processes.foreach { process =>
      val members: IndexedSeq[Member] = serviceMembers.get(Actor_Process).get
      if (members.length >= 1) {
        val member: Member = members(new Random().nextInt(members.length))
        context.actorSelection(RootActorPath(member.address) / "user" / Actor_Process) !
          ProcessMessages.WorkerJob(process, result, env, process.args)
      } else {
        log.error("process role less.")
      }
    }
  }

  def toProcess(resultOpt: Option[String], processesOpt: Option[Seq[ConfTaskAction]])(implicit env: Map[String, String]): Unit = {
    if (resultOpt.isEmpty) {
      log.warning(s"collect ${env.get("collect-name")} not result.")
    } else if (processesOpt.isEmpty) {
      log.warning(s"collect ${env.get("collect-name")} not processes.")
    } else {
      toProcess(resultOpt.get, processesOpt.get, env)
    }
  }
}
