package org.cmsfs.role.action

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberUp, UnreachableMember}
import akka.routing.FromConfig
import org.cmsfs.config.db.table.ConfTaskAction
import org.cmsfs.role.ServiceStart

class ActionService extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  val actionWorkerActorRef = context.actorOf(FromConfig.props(ProcessWorker.props), "worker")

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}.", member.address, member.roles)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case ActionMessages.WorkerJob(result, actions, env) =>
      toAction(result, actions, env)
    case _: MemberEvent => // ignore
  }

  def toAction(result: String, processes: Seq[ConfTaskAction], env: Map[String, String]): Unit = {
    processes.foreach { p =>
      val action: Action = Action(p.id, p.args, p.actions.map(x => x.map(f => Action(f.id, f.args, None))))
      actionWorkerActorRef ! WorkerAction(result, action, env)
    }
  }

  def toAction(resultOpt: Option[String], processesOpt: Option[Seq[ConfTaskAction]])(implicit env: Map[String, String]): Unit = {
    if (resultOpt.isEmpty) {
      log.warning(s"collect ${env.get("collect-name")} not result.")
    } else if (processesOpt.isEmpty) {
      log.warning(s"collect ${env.get("collect-name")} not processes.")
    } else {
      toAction(resultOpt.get, processesOpt.get, env)
    }
  }
}

object ActionService extends ServiceStart[ActionService]