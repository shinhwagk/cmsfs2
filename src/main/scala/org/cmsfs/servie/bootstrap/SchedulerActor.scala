package org.cmsfs.servie.bootstrap

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.ClusterInfo.{Actor_Collect_Local_Script, Actor_Collect_Ssh_Script, Actor_Collect_Jdbc}
import org.cmsfs.servie.collect.jdbc.CollectJdbcMessages
import org.cmsfs.servie.collect.local.script.CollectLocalScriptMessages
import org.cmsfs.servie.collect.ssh.script.CollectSshScriptMessages

class SchedulerActor extends Actor with ActorLogging {

  import SchedulerActor._

  override def receive: Receive = {
    case SchedulerCollectLocalScriptMessages(job, members) =>
      members.foreach(member =>
        context.actorSelection(RootActorPath(member.address) / "user" / Actor_Collect_Local_Script) ! job)
    case SchedulerCollectJdbc(job, members) =>
      members.foreach(member =>
        context.actorSelection(RootActorPath(member.address) / "user" / Actor_Collect_Jdbc) ! job)
    case SchedulerCollectSshScriptMessages(job, members) =>
      members.foreach(member =>
        context.actorSelection(RootActorPath(member.address) / "user" / Actor_Collect_Ssh_Script) ! job)
    case _ => println("aaaa")
  }
}

object SchedulerActor {
  val props = Props[SchedulerActor]

  case class SchedulerCollectLocalScriptMessages(job: CollectLocalScriptMessages.WorkerJob, members: IndexedSeq[Member])

  case class SchedulerCollectSshScriptMessages(job: CollectSshScriptMessages.WorkerJob, members: IndexedSeq[Member])

  case class SchedulerCollectJdbc(job: CollectJdbcMessages.WorkerJob, member: IndexedSeq[Member])

}