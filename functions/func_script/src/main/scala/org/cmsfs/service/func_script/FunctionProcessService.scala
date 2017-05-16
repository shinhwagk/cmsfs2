package org.cmsfs.service.func_script

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberUp, UnreachableMember}
import akka.routing.FromConfig

class FunctionProcessService extends Actor with ActorLogging  {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  val workers = context.actorOf(FromConfig.props(FunctionProcessWorker.props), "worker")

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}.", member.address, member.roles)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
  }
}

object FunctionProcessService {
  val props = Props[FunctionProcessService]
}