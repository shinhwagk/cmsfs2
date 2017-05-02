package org.cmsfs.role.service

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberRemoved, MemberUp, UnreachableMember}
import akka.routing.FromConfig
import org.cmsfs.role.process.ProcessMessages

class ServiceMaster extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  val worker = context.actorOf(FromConfig.props(ServiceWorker.props), "worker")

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}.", member.address, member.roles)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case job: ServiceMessages.WorkerJob => worker ! job
    case _: MemberEvent => // ignore
  }

}