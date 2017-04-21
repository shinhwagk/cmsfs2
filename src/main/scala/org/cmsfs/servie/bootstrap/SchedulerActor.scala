package org.cmsfs.servie.bootstrap

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.ClusterInfo.{Actor_Collect_Jdbc, Actor_Collect_Script_Local, Actor_Collect_Script_Remote}
import org.cmsfs.servie.collect.jdbc.CollectJdbcMessages
import org.cmsfs.servie.collect.script.local.CollectScriptLocalMessages
import org.cmsfs.servie.collect.script.remote.CollectScriptRemoteMessages

class SchedulerActor extends Actor with ActorLogging {

  import SchedulerActor._

  override def receive: Receive = {
    case SchedulerCollectScriptLocalMessages(job, members) =>
      members.foreach(member =>
        context.actorSelection(RootActorPath(member.address) / "user" / Actor_Collect_Script_Local)
          ! job)
    case RegisterCollectScriptRemote(members) =>
      members.foreach(member =>
        context.actorSelection(RootActorPath(member.address) / "user" / Actor_Collect_Script_Remote)
          ! CollectScriptRemoteMessages.WorkerJob("xxxxxxxxxx"))
    case RegisterCollectJdbc(members) =>
      members.foreach(member =>
        context.actorSelection(RootActorPath(member.address) / "user" / Actor_Collect_Jdbc)
          ! CollectJdbcMessages.WorkerJob("aaa"))
    case RegisterCollectScriptRemote(members) => println("111")
    case _ => println("aaaa")
    //      FormatScripts :+ member
  }
}

object SchedulerActor {
  val props = Props[SchedulerActor]

  case class SchedulerCollectScriptLocalMessages(job: CollectScriptLocalMessages.WorkerJob, members: IndexedSeq[Member])

  case class RegisterCollectScriptRemote(members: IndexedSeq[Member])

  case class RegisterFormatScript(member: IndexedSeq[Member])

  case class RegisterCollectJdbc(member: IndexedSeq[Member])

}