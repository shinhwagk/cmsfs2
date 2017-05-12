package org.cmsfs.role.collect

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.routing.FromConfig
import org.cmsfs.config.db.table.ConfTaskAction
import org.cmsfs.role.process.ProcessMessages

import scala.concurrent.duration._


trait CollectServiceCore extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

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

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}, role: {}.", member.address, member.roles)
    case UnreachableMember(member) =>
      log.error("Member detected as unreachable: {}, role: {}.", member, member.roles)
    case msg: CollectorWorkerMessage.WorkerJob => worker ! msg
    case a@(s: String, q: Seq[ConfTaskAction], f: Map[String, String]) =>
      toProcess(s,q,f)
    case _: MemberEvent => // ignore
  }

  val actionActor = context.actorOf(FromConfig.props(), "action")

  def toProcess(result: String, processes: Seq[ConfTaskAction], env: Map[String, String]): Unit = {
    processes.foreach { process =>
      actionActor ! ProcessMessages.WorkerJob(process, result, env, process.args)
    }
  }

  def toProcess(resultOpt: Option[String], processesOpt: Option[Seq[ConfTaskAction]])(implicit env: Map[String, String]): Unit = {
    if (resultOpt.isEmpty) {
      log.warning(s"collect ${env.get("collect-name")} not result.")
    } else if (processesOpt.isEmpty) {
      log.warning(s"collect ${env.get("collect-name")} not processes.")
    } else {
      toProcess(resultOpt.get, processesOpt.get, env)
    }
  }
}
