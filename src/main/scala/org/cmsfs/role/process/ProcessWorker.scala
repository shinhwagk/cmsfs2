package org.cmsfs.role.process

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.ClusterInfo
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.role.process.ProcessMessages.WorkerJob
import org.cmsfs.role.process.Processor.{ProcessConfig, ProcessorConfig}
import org.cmsfs.role.service.ServiceMessages

import scala.collection.mutable
import scala.util.{Failure, Random, Success}

class ProcessorWorker(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) extends Actor with ActorLogging {
  println("ProcessWorker start.")

  import context.dispatcher

  override def receive: Receive = {
    case WorkerJob(confTaskProcess, result, env, argsOpt) =>
      QueryConfig.getCoreProcessById(confTaskProcess.id).onComplete {
        case Success(coreProcess) => {
          val processConfig = ProcessConfig(coreProcess.files, confTaskProcess.args)
          val processorConfig = ProcessorConfig(result, processConfig, env)
          try {
            val processResult: Option[String] = Processor.executeProcess(processorConfig)
            if (processResult.isDefined) {
              confTaskProcess.services.foreach(_.foreach { confService =>
                val members = serviceMembers.get(ClusterInfo.Service_Service).get
                if (members.length >= 1) {
                  val member = members(new Random().nextInt(members.length))
                  context.actorSelection(RootActorPath(member.address) / "user" / ClusterInfo.Actor_Service) !
                    ServiceMessages.WorkerJob(confService, env, processResult.get)
                  //                println("process ", env.get("collect-name"), env.get("conn-name"), env.get("utc-date"), new Date().toInstant.toString)
                }
              })
            }
          } catch {
            case ex: Exception => println("process execute error: " + ex.getMessage)
          }
        }
        case Failure(ex) =>
          log.error(s"process ${ex.getMessage}")
      }
  }
}

object ProcessorWorker {
  def props(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) = Props(new ProcessorWorker(serviceMembers))
}