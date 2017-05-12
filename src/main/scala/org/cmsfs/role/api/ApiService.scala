package org.cmsfs.role.api

import java.util.Date

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberUp, UnreachableMember}
import org.cmsfs.Common
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.role.ServiceStart
import org.quartz.CronExpression

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class ApiService extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  import context.dispatcher

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}", member.address, member.roles)
    case UnreachableMember(member) =>
      log.error("Member detected as unreachable: {}", member, member.status, member.roles)
    case Api.getAllValidCollect(cDate) =>
      implicit val data = cDate
      val r = sender()
      QueryConfig.getConfBootstrap onComplete {
        case Success(bootstrap) =>
          r ! bootstrap.filter(d => filterCron(d.cron))
        case Failure(ex) => log.error(ex.getMessage)
      }
    case _: MemberEvent => // ignore
  }

  def filterCron(cron: String)(implicit cDate: Date): Boolean = {
    new CronExpression(cron).isSatisfiedBy(cDate)
  }
}

object ApiService extends ServiceStart[ApiService]