package org.cmsfs.servie.alarm

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberRemoved, MemberUp, UnreachableMember}
import akka.routing.FromConfig
import org.cmsfs.ClusterInfo._
import org.cmsfs.Common

class AlarmService extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  val worker = context.actorOf(FromConfig.props(AlarmWorker.props), "worker")

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}.", member.address, member.roles)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case job: AlarmMessages.WorkerJob => worker ! job
    case _: MemberEvent => // ignore
  }
}

object AlarmService {
  def main(args: Array[String]): Unit = {
    val seed = args(0)
    val port = args(1)
    val system = Common.genActorSystem(Role_Alarm, seed, port)
    system.actorOf(Props[AlarmService], name = Actor_Alarm)
  }
}