package org.cmsfs.role.bootstrap

import java.util.Date

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberUp, ReachableMember, UnreachableMember}
import akka.routing.FromConfig
import org.cmsfs.config.db.table.ConfBootstrap
import org.cmsfs.role.ServiceStart
import org.cmsfs.role.api.Api
import org.cmsfs.role.bootstrap.BootstrapService.CollectScheduler
import org.cmsfs.role.trigger.collect.{CollectorMasterService, CollectorServiceMessage}
import org.quartz.CronExpression

import scala.concurrent.duration._

class BootstrapService extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  import context.dispatcher

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember], classOf[ReachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  Cluster.get(context.system).registerOnMemberUp {
    context.system.scheduler.schedule(0.seconds, 1.seconds, self, CollectScheduler)
  }

  val apiActor = context.actorOf(FromConfig.props(), name = "api")

  val collectServiceActor = context.actorOf(CollectorMasterService.props, "collect-service")

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}", member.address, member.roles)
    case ReachableMember(member) =>
      log.info("Member is Reachable: {}. role: {}", member.address, member.roles)
    case UnreachableMember(member) =>
      log.error("Member detected as unreachable: {}", member, member.status, member.roles)
    case _: MemberEvent => // ignore
    case CollectScheduler => {
      log.info(s"scheduler ${System.currentTimeMillis()}")
      schedulerAction
    }
  }

  def schedulerAction = {
    import akka.pattern.ask
    import akka.util.Timeout
    implicit val timeout = Timeout(5 seconds)
    implicit val cDate = new Date()
    (apiActor ? Api.getAllValidCollect(cDate)).mapTo[Seq[ConfBootstrap]] foreach { tasks =>
      tasks foreach { task =>
        collectServiceActor ! CollectorServiceMessage.WorkerJob(task.schema, cDate.toInstant.toString)
      }
    }
  }

  def filterCron(cron: String)(implicit cDate: Date): Boolean = {
    new CronExpression(cron).isSatisfiedBy(cDate)
  }
}

object BootstrapService extends ServiceStart[BootstrapService] {

  case object CollectScheduler

}