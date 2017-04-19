package org.cmsfs.servie.collect

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}
import org.cmsfs.Common
import org.cmsfs.servie.CmsfsClusterInfo.Role_Format_Script
import org.cmsfs.servie.collect.script.local.CollectScriptLocalMessages
import org.cmsfs.servie.collect.script.remote.CollectScriptRemoteMessages

trait CollectActorCore extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  val worker: ActorRef;

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp], classOf[MemberJoined], classOf[MemberEvent], classOf[UnreachableMember], classOf[MemberRemoved])

  override def postStop(): Unit = cluster.unsubscribe(self)

  var FormatScriptMembers: IndexedSeq[Member] = IndexedSeq.empty[Member]

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}.", member.address, member.roles)
      FormatScriptMembers = Common.registerMember(member, Role_Format_Script, FormatScriptMembers)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
      FormatScriptMembers = Common.unRegisterMember(member, Role_Format_Script, FormatScriptMembers)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case workerMessage: CollectWorkerMessage =>
      println("collect mgs", workerMessage,worker)
      workerMessage match {
        case mgs: CollectScriptLocalMessages.WorkerJob => worker ! mgs
        case mgs: CollectScriptRemoteMessages.WorkerJob => worker ! mgs
        case _ => ???
      }
    case _: MemberEvent => // ignore
  }
}
