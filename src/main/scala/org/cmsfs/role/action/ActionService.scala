package org.cmsfs.role.action

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberUp, UnreachableMember}
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.config.db.table.ConfTaskAction
import org.cmsfs.role.ServiceStart
import org.cmsfs.role.action.Processor.{ActionConfig, ProcessorConfig}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class ActionService extends Actor with ActorLogging {

  import context.dispatcher

  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}.", member.address, member.roles)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case ActionMessages.WorkerJob(result, action, env) =>
      val p: ConfTaskAction = action
      val actions: Action = Action(p.id, p.args, p.actions.map(x => x.map(f => Action(f.id, f.args, None))))
      self ! WorkerAction(result, env, actions)
    case w: WorkerAction =>
      processAction(w)
    case _: MemberEvent => // ignore
  }

  def processAction(wa: WorkerAction): Unit = {
    val action = wa.action
    val result = wa.collectResult
    val env = wa.evn
    val processResult: Future[Option[String]] = for {
      coreProcess <- QueryConfig.getCoreProcessById(action.id)
    } yield {
      val processConfig = ActionConfig(coreProcess.files, None)
      val processorConfig = ProcessorConfig(result, processConfig, env)
      Processor.executeProcess(processorConfig)
    }

    processResult onComplete {
      case Success(rOpt) =>
        for {r <- rOpt; actions <- action.actions}
          yield actions foreach (action => processAction(WorkerAction(r, env, action)))
      case Failure(ex) => log.error(ex.getMessage)
    }
  }
}

object ActionService extends ServiceStart[ActionService]