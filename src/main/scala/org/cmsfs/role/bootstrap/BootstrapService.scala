package org.cmsfs.role.bootstrap

import java.util.Date

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.ClusterEvent.{MemberEvent, MemberUp, ReachableMember, UnreachableMember}
import akka.cluster.{Cluster, Member}
import org.cmsfs.ClusterInfo._
import org.cmsfs.config.db._
import org.cmsfs.role.bootstrap.BootstrapService.CollectScheduler
import org.cmsfs.role.collect.{CollectorService, CollectorServiceMessage}
import org.cmsfs.{ClusterInfo, Common}
import org.quartz.CronExpression

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class BootstrapService extends Actor with ActorLogging {

  /**
    * self service need services.
    *
    **/
  val memberNames: Seq[String] =
    Service_Collect_Ssh ::
      Service_Collect_Jdbc ::
      Service_Collect_Local ::
      Nil

  val serviceMembers: mutable.Map[String, IndexedSeq[Member]] = Common.initNeedServices(memberNames)

  val cluster = Cluster(context.system)

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember], classOf[ReachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  import context.dispatcher

  val collectServiceActor: ActorRef = context.actorOf(CollectorService.props(serviceMembers), "collect-service")

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}", member.address, member.roles)
      Common.registerMember(member, serviceMembers)
    //    case state: CurrentClusterState =>
    //      log.info("Current members: {}", state.members.mkString(", "))
    //      state.members.filter(_.status == MemberStatus.Up).foreach(member => Common.registerMember(member, serviceMembers))
    case ReachableMember(member) =>
      log.info("Member is Reachable: {}. role: {}", member.address, member.roles)
    case UnreachableMember(member) =>
      log.error("Member detected as unreachable: {}", member, member.status, member.roles)
      Common.unRegisterMember(member, serviceMembers)
    case _: MemberEvent => // ignore
    case CollectScheduler => {
      log.info(s"scheduler ${System.currentTimeMillis()}")
      schedulerAction
    }
  }

  def schedulerAction = {
    implicit val cDate = new Date()
    QueryConfig.getConfBootstrap onComplete {
      case Success(bootstrap) =>
        bootstrap.filter(d => filterCron(d.cron)).foreach { task =>
          collectServiceActor ! CollectorServiceMessage.WorkerJob(task.schema, cDate.toInstant.toString)
        }
      case Failure(ex) => log.error(ex.getMessage)
    }
  }

  def filterCron(cron: String)(implicit cDate: Date): Boolean = {
    new CronExpression(cron).isSatisfiedBy(cDate)
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

    system.scheduler.schedule(0.seconds, 1.seconds, bootstrap, CollectScheduler)
  }

  case object CollectScheduler

}