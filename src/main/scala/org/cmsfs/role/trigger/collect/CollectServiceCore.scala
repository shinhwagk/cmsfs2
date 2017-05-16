package org.cmsfs.role.trigger.collect

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.routing.FromConfig
import org.cmsfs.role.action.ActionMessages

trait CollectServiceCore extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  val collectServiceName = this.getClass.getName

  val worker: ActorRef;

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}, role: {}.", member.address, member.roles)
    case UnreachableMember(member) =>
      log.error("Member detected as unreachable: {}, role: {}.", member, member.roles)
    case msg: CollectorWorkerMessage.WorkerJob => worker ! msg
    case a: ActionMessages.WorkerJob => actionActor ! a
    case _: MemberEvent => // ignore
  }

  val actionActor = context.actorOf(FromConfig.props(), "action")
}
