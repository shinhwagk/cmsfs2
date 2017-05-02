package org.cmsfs.role.process

import akka.actor.{Actor, ActorLogging, Props}
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.role.process.ProcessMessages.WorkerJob
import org.cmsfs.role.process.Processor.{ProcessConfig, ProcessorConfig}

class ProcessorWorker extends Actor with ActorLogging {
  println("ProcessWorker start.")

  import context.dispatcher

  override def receive: Receive = {
    case WorkerJob(confTaskProcess, result, env) =>
      println(s"result ${result} ${confTaskProcess.args}")
      println(s"result ${confTaskProcess.args}")
      QueryConfig.getCoreProcessById(confTaskProcess.id).foreach { coreProcess =>
        println(coreProcess)
        val processConfig = ProcessConfig(coreProcess.files, confTaskProcess.args)
        val processorConfig = ProcessorConfig(result, processConfig, env)
//        Processor.executeProcess(processorConfig)
      }
  }
}

object ProcessorWorker {
  val props = Props[ProcessorWorker]
}