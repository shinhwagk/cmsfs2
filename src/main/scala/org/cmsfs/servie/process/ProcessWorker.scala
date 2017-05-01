package org.cmsfs.servie.process

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.servie.process.ProcessMessages.WorkerJob
import org.cmsfs.servie.process.Processor.{ProcessConfig, ProcessorConfig}
import org.cmsfs.servie.terminal.TerminalMessages

import scala.collection.mutable

class ProcessorWorker(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) extends Actor with ActorLogging {
  println("ProcessWorker start.")

  import context.dispatcher

  override def receive: Receive = {
    case WorkerJob(confTaskProcess, result, env) =>
      QueryConfig.getCoreProcessById(confTaskProcess.processId).foreach { coreProcess =>
        val processConfig = ProcessConfig(coreProcess.path, coreProcess.files, confTaskProcess.args)
        val processorConfig = ProcessorConfig(result, processConfig, env)
        val processResults = Processor.executeProcess(processorConfig)

        val members = serviceMembers.get("service").get
        val member = members(0)

        confTaskProcess.terminals.foreach { service =>
          context.actorSelection(RootActorPath(member.address) / "user" / "") ! TerminalMessages.WorkerJob(service, result, env)
        }

      }
  }
}

object ProcessorWorker {
  def props(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) = Props(new ProcessorWorker(serviceMembers))
}