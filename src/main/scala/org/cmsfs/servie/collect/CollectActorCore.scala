package org.cmsfs.servie.collect

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import org.cmsfs.ClusterInfo._
import org.cmsfs.Common
import org.cmsfs.servie.collect.jdbc.CollectJdbcMessages
import org.cmsfs.servie.collect.local.script.CollectLocalScriptMessages
import org.cmsfs.servie.collect.ssh.script.CollectSshScriptMessages

trait CollectActorCore extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  val worker: ActorRef;

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp], classOf[MemberJoined], classOf[MemberEvent], classOf[UnreachableMember], classOf[MemberRemoved])

  override def postStop(): Unit = cluster.unsubscribe(self)

  val memberNames = Service_Format_Script :: Nil

  val serviceMembers = Common.initNeedServices(memberNames)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}.", member.address, member.roles)
      Common.registerMember(member, serviceMembers)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
      Common.unRegisterMember(member, serviceMembers)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case workerMessage: CollectWorkerMessage =>
      workerMessage match {
        case mgs: CollectLocalScriptMessages.WorkerJob => worker ! mgs
        case mgs: CollectSshScriptMessages.WorkerJob => worker ! mgs
        case mgs: CollectJdbcMessages.WorkerJob => worker ! mgs
        case _ => ???
      }
    case _: MemberEvent => // ignore
  }
}
