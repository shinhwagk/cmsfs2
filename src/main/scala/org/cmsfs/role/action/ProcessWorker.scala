package org.cmsfs.role.action

import akka.actor.{Actor, ActorLogging, Props}
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.role.action.Processor.{ActionConfig, ProcessorConfig}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class ProcessWorker extends Actor with ActorLogging {

  import context.dispatcher

  override def receive: Receive = {
    case wa: WorkerAction => processAction(wa)
  }

  def processAction(wa: WorkerAction): Unit = {
    val action = wa.action
    val result = wa.data
    val env = wa.env
    val processResult: Future[String] = for {
      coreProcess <- QueryConfig.getCoreProcessById(action.id)
    } yield {
      val processConfig = ActionConfig(coreProcess.files, None)
      val processorConfig = ProcessorConfig(result, processConfig, env)
      Processor.executeProcess(processorConfig)
    }

    processResult onComplete {
      case Success(rs) =>
        for {actions <- action.actions}
          yield actions foreach (action => processAction(WorkerAction(rs, action, env)))
      case Failure(ex) => log.error(ex.getMessage)
    }
  }
}

object ProcessWorker {
  val props = Props[ProcessWorker]
}