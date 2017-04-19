package org.cmsfs.servie.bootstrap

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.servie.CmsfsClusterInfo.{Actor_Collect_Script_Local, Actor_Collect_Script_Remote}
import org.cmsfs.servie.collect.script.local.CollectScriptLocalMessages
import org.cmsfs.servie.collect.script.remote.CollectScriptRemoteMessages

class SchedulerActor extends Actor with ActorLogging {

  import SchedulerActor._

  override def receive: Receive = {
    case RegisterCollectScriptLocal(collectName, members) =>
      members.foreach(member => context.actorSelection(RootActorPath(member.address) / "user" / Actor_Collect_Script_Local) ! CollectScriptLocalMessages.WorkerJob(collectName, Seq("a", "b", "c", "x"), "x"))
    case RegisterCollectScriptRemote(members) =>
      println("xxxxx" ,members)
      members.foreach(member => context.actorSelection(RootActorPath(member.address) / "user" / Actor_Collect_Script_Remote) ! CollectScriptRemoteMessages.WorkerJob("xxxxxxxxxx"))
    case _ => println("aaaa")
    //      FormatScripts :+ member
  }
}

object SchedulerActor {
  val props = Props[SchedulerActor]

  case class RegisterCollectScriptLocal(name: String, members: IndexedSeq[Member])

  case class RegisterCollectScriptRemote(members: IndexedSeq[Member])

  case class RegisterFormatScript(member: Member)

}