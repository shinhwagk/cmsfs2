package org.cmsfs.role.collect

import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import org.cmsfs.ClusterInfo._
import org.cmsfs.Common

import scala.concurrent.duration._


trait CollectServiceActorCore extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  val selfPathName = "worker"

  val collectServiceName = this.getClass.getName

  val worker: ActorRef;

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case ex: Exception =>
        log.error(s"${collectServiceName}: " + ex.getMessage)
        Restart
    }

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember])

  override def postStop(): Unit = {
    log.error(s"${collectServiceName} close.")
    cluster.unsubscribe(self)
  }

  val memberNames = Service_Process :: Nil

  val serviceMembers = Common.initNeedServices(memberNames)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}, role: {}.", member.address, member.roles)
      Common.registerMember(member, serviceMembers)
    case UnreachableMember(member) =>
      log.error("Member detected as unreachable: {}, role: {}.", member, member.roles)
      Common.unRegisterMember(member, serviceMembers)
    //    case MemberRemoved(member, previousStatus) =>
    //      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case msg: CollectorWorkerMessage.WorkerJob => worker ! msg
    case _: MemberEvent => // ignore
  }
}
