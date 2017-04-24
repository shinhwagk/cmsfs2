package org.cmsfs.servie.bootstrap

import java.util.Date

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.routing.{FromConfig, RoundRobinPool}
import org.cmsfs.ClusterInfo._
import org.cmsfs.config.QueryConfig
import org.cmsfs.config.db.{CoreCollect, CoreConnectorSsh, CoreMonitorDetail}
import org.cmsfs.servie.bootstrap.BootstrapService.MessageScheduler
import org.cmsfs.servie.bootstrap.SchedulerActor.{SchedulerCollectLocalScriptMessages, SchedulerCollectSshScriptMessages}
import org.cmsfs.servie.collect.jdbc.CollectJdbcWorker
import org.cmsfs.servie.collect.local.script.CollectLocalScriptMessages
import org.cmsfs.servie.collect.ssh.script.CollectSshScriptMessages
import org.cmsfs.servie.collect.ssh.script.CollectSshScriptMessages.Collector
import org.cmsfs.{ClusterInfo, Common}
import org.quartz.CronExpression

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class BootstrapService extends Actor with ActorLogging {

  val memberNames: Seq[String] = Service_Collect_Ssh_Script ::
    Service_Collect_Local_Script ::
    Service_Collect_Jdbc ::
    Nil

  val serviceMembers: mutable.Map[String, IndexedSeq[Member]] = Common.initNeedServices(memberNames)

  val cluster = Cluster(context.system)

  import context.dispatcher

  val schedulerActor: ActorRef = context.actorOf(SchedulerActor.props, "scheduler")

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp],
      classOf[MemberJoined], classOf[MemberEvent], classOf[UnreachableMember], classOf[MemberRemoved],
      classOf[MemberLeft], classOf[MemberExited])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberJoined(member) =>
      log.info("Member is Join: {}. role: {}", member.address, member.roles)
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}", member.address, member.roles)
      Common.registerMember(member, serviceMembers)
    case state: CurrentClusterState =>
      log.info("Current members: {}", state.members.mkString(", "))
      state.members.filter(_.status == MemberStatus.Up).foreach(member => Common.registerMember(member, serviceMembers))
    case UnreachableMember(member) =>
      println("Member detected as unreachable: {}", member, member.status)
      Common.unRegisterMember(member, serviceMembers)
    case MemberLeft(member) =>
      println("Member is Leaving: {} ", member.address, member.status)
    case MemberExited(member) =>
      println("Member is Exited: {} ", member.address, member.status)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case MessageScheduler =>
      schedulerAction()
    case _: MemberEvent => // ignore
  }

  def schedulerAction() = {
    val cDate = new Date()
    QueryConfig.getCoreMonitorDetails.foreach { cmds =>
      val validMonitorDetails: Seq[CoreMonitorDetail] = filterMonitorDetails(cDate)(cmds)
      validMonitorDetails.foreach { md =>
        md.connectorMode match {
          case "ssh" =>
            val f: Future[(CoreCollect, Seq[CoreConnectorSsh])] = for {
              collect <- QueryConfig.getCoreCollectById(md.collectId)
              conns <- Future.sequence(md.connectorIds.map(id => QueryConfig.getCoreConnectorSshById(id)))
            } yield (collect, conns)
            f.foreach { case (collect, conns) =>
              for (conn <- conns) {
                val n_collect = CollectSshScriptMessages.Collector(collect.name, collect.path, collect.args)
                val n_connector = CollectSshScriptMessages.Connector(conn.name, conn.ip, conn.port, conn.user, conn.password, conn.privateKey)
                schedulerActor ! SchedulerCollectSshScriptMessages(CollectSshScriptMessages.WorkerJob(collect.name, n_connector, n_collect, "aa"),
                  serviceMembers.get(Service_Collect_Ssh_Script).get)
              }
            }
          //          case "jdbc" =>
          case _ =>
            println(md.connectorMode)
        }
      }
    }
  }

  def filterCron(cron: String, cDate: Date): Boolean = {
    new CronExpression(cron).isSatisfiedBy(cDate)
  }

  def filterMonitorDetails(cDate: Date)(monitorDetails: Seq[CoreMonitorDetail]): Seq[CoreMonitorDetail] = {
    val view = monitorDetails.filter(cmd => filterCron(cmd.cron, cDate))
    log.info(s"filter monitor detail ${view.map(_.id).toString()} ${cDate.toString} - ${view.map(_.collectId).toString()}")
    view
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