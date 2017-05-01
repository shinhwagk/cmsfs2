package org.cmsfs.servie.bootstrap

import java.util.Date

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.cmsfs.config.db._
import org.cmsfs.servie.bootstrap.BootstrapService.MessageScheduler
import org.cmsfs.servie.collect.{CollectorService, CollectorServiceMessage}
import org.cmsfs.{ClusterInfo, Common}
import org.quartz.CronExpression

import scala.concurrent.duration._

class BootstrapService extends Actor with ActorLogging {

  import context.dispatcher

  val schedulerActor: ActorRef = context.actorOf(CollectorService.props, "scheduler")

  def schedulerAction() = {
    implicit val cDate = new Date()
    QueryConfig.getConfTasks.foreach(_.filter(d => filterCron(d.cron)).foreach(task => schedulerActor !
      CollectorServiceMessage.WorkerJob(task.schema, cDate.toInstant.toString)))
  }

  def filterCron(cron: String)(implicit cDate: Date): Boolean = {
    new CronExpression(cron).isSatisfiedBy(cDate)
  }

  override def receive: Receive = {
    case MessageScheduler =>
      schedulerAction()
  }
}

object BootstrapService {

  import ClusterInfo._

  def main(args: Array[String]): Unit = {
    val seed = args(0)
    val port = args(1)
    val system = Common.genActorSystem(Role_Bootstrap, seed, port)
    val bootstrap = system.actorOf(Props[BootstrapService], name = Service_Bootstrap)

    import system.dispatcher

    system.scheduler.schedule(0.seconds, 1.seconds, bootstrap, MessageScheduler)
  }

  case object MessageScheduler

}