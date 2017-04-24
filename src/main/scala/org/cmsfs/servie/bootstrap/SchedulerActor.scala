package org.cmsfs.servie.bootstrap

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.ClusterInfo.{Actor_Collect_Script_Local, Actor_Collect_Ssh_Script}
import org.cmsfs.servie.collect.local.script.CollectLocalScriptMessages
import org.cmsfs.servie.collect.ssh.script.CollectSshScriptMessages

class SchedulerActor extends Actor with ActorLogging {

  import SchedulerActor._

  override def receive: Receive = {
    case SchedulerCollectLocalScriptMessages(job, members) =>
      members.foreach(member =>
        context.actorSelection(RootActorPath(member.address) / "user" / Actor_Collect_Script_Local) ! job)
    case SchedulerCollectJdbc(members) =>
    //      members.foreach(member => ???
    //        context.actorSelection(RootActorPath(member.address) / "user" / Actor_Collect_Jdbc)
    //          ! CollectJdbcMessages.WorkerJob("aaa")
    //      )
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

  case class SchedulerFormatScript(member: IndexedSeq[Member])

  case class SchedulerCollectJdbc(member: IndexedSeq[Member])

}