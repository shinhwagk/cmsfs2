package org.cmsfs.servie.process

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberRemoved, MemberUp, UnreachableMember}
import akka.routing.FromConfig
import org.cmsfs.ClusterInfo._
import org.cmsfs.Common

class ProcessService extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  val worker = context.actorOf(FromConfig.props(ProcessorWorker.props), "worker")

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
    case job: ProcessMessages.WorkerJob => worker ! job
    case _: MemberEvent => // ignore
  }
}

object ProcessService {
  def main(args: Array[String]): Unit = {
    val seed = args(0)
    val port = args(1)
    val system = Common.genActorSystem(Role_Process, seed, port)
    system.actorOf(Props[ProcessService], name = Actor_Process)
  }
}