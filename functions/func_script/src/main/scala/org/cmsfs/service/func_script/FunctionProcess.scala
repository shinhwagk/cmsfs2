package org.cmsfs.service.func_script

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberUp, UnreachableMember}

class FunctionProcess extends Actor with ActorLogging with ProcessRunner {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}.", member.address, member.roles)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case _: Seq[String] => executeProcess(Seq("x"): _*).recover { case xc => xc.getMessage; None }
      sender() ! "some"
  }
}

object FunctionProcess {
  val props = Props[FunctionProcess]
}