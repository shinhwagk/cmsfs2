package org.cmsfs.servie.collect.ssh

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.servie.collect.{Collector, CollectorWorkerMessage}
import org.cmsfs.servie.collect.CollectorWorkerMessage.WorkerJob
import org.cmsfs.servie.process.ProcessMessages
import org.cmsfs.ClusterInfo._

import scala.collection.mutable
import scala.util.{Failure, Random, Success}

class SshCollectWorker(serviceMembers: mutable.Map[String, IndexedSeq[Member]])
  extends Actor with ActorLogging {

  log.info("SshCollectWorker start.")

  import context.dispatcher

  //  var processCapacityCalculate = 0L

  override def receive: Receive = {
    case CollectorWorkerMessage.WorkerJob(task, conf, collectorEnv) =>
      println("CollectorWorkerMessage")

      val connectId = task.collect.connect.get

      QueryConfig.getCoreConnectorSshById(connectId).foreach { conn =>
        val collectConfig = conf
        val env = collectorEnv.copy(connectName = Some(conn.name))
        val config: Collector.CollectorConfig = Collector.SshCollectorConfig(conn, collectConfig, env)
        val resultOpt: Option[String] = Collector.executeCollect(config)()
        resultOpt.foreach { result =>
          task.processes.foreach { process =>
            val formatMembers = serviceMembers.get(Actor_Process).get
            if (formatMembers.length >= 1) {
              val member = formatMembers(new Random().nextInt(formatMembers.length))
              context.actorSelection(RootActorPath(member.address) / "user" / Actor_Process) ! ProcessMessages.WorkerJob(process, result, env)
            }
          }
        }
        //        task.processes.foreach{process=>
        //          val formatMembers = serviceMembers.get(Actor_Process).get
        //          if (formatMembers.length >= 1) {
        //            val member = formatMembers(new Random().nextInt(formatMembers.length))
        //            context.actorSelection(RootActorPath(member.address) / "user" / Actor_Process) ! ProcessMessages.WorkerJob(process, result.get, env)
        //          }
        //        }
      }
  }
}

object SshCollectWorker {
  def props(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) = Props(new SshCollectWorker(serviceMembers))
}