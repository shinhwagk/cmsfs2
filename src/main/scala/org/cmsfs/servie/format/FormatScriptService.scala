package org.cmsfs.servie.format

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberRemoved, MemberUp, UnreachableMember}
import org.cmsfs.{ClusterInfo, Common}
import ClusterInfo._
import akka.routing.FromConfig

class FormatScriptService extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  val memberNames = Service_Alarm :: Service_Elastic :: Nil

  val serviceMembers = Common.initNeedServices(memberNames)

  val worker = context.actorOf(FromConfig.props(FormatScriptWorker.props(serviceMembers)), "worker")

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}.", member.address, member.roles)
      Common.registerMember(member, serviceMembers)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
      Common.unRegisterMember(member, serviceMembers)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case job: FormatScriptMessages.WorkerJob => worker ! job
    case _: MemberEvent => // ignore
  }
}

object FormatScriptService {
  def main(args: Array[String]): Unit = {
    val seed = args(0)
    val port = args(1)
    val system = Common.genActorSystem(Role_Format_Script, seed, port)
    system.actorOf(Props[FormatScriptService], name = Actor_Format_Script)
  }
}