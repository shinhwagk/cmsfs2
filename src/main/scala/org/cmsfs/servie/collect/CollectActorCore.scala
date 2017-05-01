package org.cmsfs.servie.collect

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.{Cluster, Member}
import akka.cluster.ClusterEvent._
import org.cmsfs.ClusterInfo._
import org.cmsfs.Common

import scala.collection.mutable

trait CollectActorCore extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  val worker: ActorRef;

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp], classOf[MemberJoined], classOf[MemberEvent], classOf[UnreachableMember], classOf[MemberRemoved])

  override def postStop(): Unit = cluster.unsubscribe(self)

  val memberNames = Service_Process :: Nil

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
    case msg: CollectorWorkerMessage.WorkerJob => worker ! msg
    case _: MemberEvent => // ignore
  }
}
