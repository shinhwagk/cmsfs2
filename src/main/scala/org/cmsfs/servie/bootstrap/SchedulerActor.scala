package org.cmsfs.servie.bootstrap

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.ClusterInfo.{Actor_Collect_Jdbc, Actor_Collect_Script_Local, Actor_Collect_Script_Remote}
import org.cmsfs.servie.collect.jdbc.CollectJdbcMessages
import org.cmsfs.servie.collect.local.script.CollectLocalScriptMessages

class SchedulerActor extends Actor with ActorLogging {

  import SchedulerActor._

  override def receive: Receive = {
    case SchedulerCollectScriptLocalMessages(job, members) =>
      members.foreach(member =>
        context.actorSelection(RootActorPath(member.address) / "user" / Actor_Collect_Script_Local)
          ! job)
    case SchedulerCollectJdbc(members) =>
      members.foreach(member => ???
//        context.actorSelection(RootActorPath(member.address) / "user" / Actor_Collect_Jdbc)
//          ! CollectJdbcMessages.WorkerJob("aaa")
     )
    case SchedulerCollectScriptRemote(members) => println("111")
    case _ => println("aaaa")
    //      FormatScripts :+ member
  }
}

object SchedulerActor {
  val props = Props[SchedulerActor]

  case class SchedulerCollectScriptLocalMessages(job: CollectLocalScriptMessages.WorkerJob, members: IndexedSeq[Member])

  case class SchedulerCollectScriptRemote(members: IndexedSeq[Member])

  case class SchedulerFormatScript(member: IndexedSeq[Member])

  case class SchedulerCollectJdbc(member: IndexedSeq[Member])

}