package org.cmsfs.role.process

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberRemoved, MemberUp, UnreachableMember}
import org.cmsfs.ClusterInfo._
import org.cmsfs.Common
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.role.ServiceStart
import org.cmsfs.role.process.Processor.{ProcessConfig, ProcessorConfig}

import scala.concurrent.Future

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
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case ProcessMessages.WorkerJob(confTaskProcess, result, env, argsOpt) =>
      val p = confTaskProcess
      val actions: Action = Action(p.id, p.args, p.actions.map(x => x.map(f => Action(f.id, f.args, None))))
      self ! WorkerAction(result, env, actions)
    case w: WorkerAction =>
      processAction(w)
    case _: MemberEvent => // ignore
  }

  def processAction(wa: WorkerAction): Unit = {
    val action = wa.action
    println(action.id)
    val result = wa.collectResult
    val env = wa.evn
    val processResult: Future[Option[String]] = for {
      coreProcess <- QueryConfig.getCoreProcessById(action.id)
    } yield {
      val processConfig = ProcessConfig(coreProcess.files, None)
      val processorConfig = ProcessorConfig(result, processConfig, env)
      Processor.executeProcess(processorConfig)
    }

    processResult.foreach { rOpt =>
      for {
        r <- rOpt
        actions <- action.actions
      } yield actions foreach { self ! WorkerAction(r, env, _) }
    }
  }
}

object ActionService extends ServiceStart[ActionService]