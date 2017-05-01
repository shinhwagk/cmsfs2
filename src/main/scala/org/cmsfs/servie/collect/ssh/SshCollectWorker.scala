package org.cmsfs.servie.collect.ssh

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.servie.collect.Collector
import org.cmsfs.servie.collect.CollectorWorkerMessage.WorkerJob
import org.cmsfs.servie.process.ProcessMessages

import scala.collection.mutable
import scala.util.Random

class SshCollectWorker(serviceMembers: mutable.Map[String, IndexedSeq[Member]])
  extends Actor with ActorLogging {

  import context.dispatcher

  //  var processCapacityCalculate = 0L

  override def receive: Receive = {
    case WorkerJob(task, conf, collectorEnv) =>
      val connectId = task.collect.collectId
      QueryConfig.getCoreConnectorSshById(connectId).foreach { conn =>
        val collectConfig = conf
        val sshConnect = SshConnect(conn.ip, conn.port, conn.user, conn.password, conn.privateKey)
        val env = collectorEnv.copy(connectName = Some(conn.name))
        val config: Collector.CollectorConfig = Collector.SshCollectorConfig(sshConnect, collectConfig, env)
        val result = Collector.executeCollect(config)()
        val formatMembers = serviceMembers.get("").get
        if (formatMembers.length >= 1) {
          task.processes.foreach { p =>
            val random_index = new Random().nextInt(formatMembers.length)
            val member = formatMembers(random_index)
            //              serviceMembers.get("process").get
            context.actorSelection(RootActorPath(member.address) / "user" / "xxx") !
              ProcessMessages.WorkerJob(p, result, env)
            //            }
          }
        }
      }
  }

  //    case WorkerJob(collectorConfig) =>
  //      val rs = Collector.executeCollect(collectorConfig)()
  //
  //      val formatMembers = serviceMembers.get("xxx").get
  //      if (formatMembers.length >= 1) {
  //        QueryConfig
  //        .
  //        //        val random_index = new Random().nextInt(formatMembers.length)
  //        //        val member = formatMembers(random_index)
  //        //        context.actorSelection(RootActorPath(member.address) / "user" / "xxx") !
  //        //          ProcessMessages.WorkerJob(ps(1), rs, "")
  //      }

  //      processCapacityCalculate += 1
  //
  //      val formatMembers = serviceMembers.get(Service_Format_Script).get
  //      if (formatMembers.length >= 1) {
  //        val path = job.collect.path
  //        val args = job.collect.args
  //        val ip = job.connect.ip
  //        val port = job.connect.port
  //        val user = job.connect.username
  //        val rs: Option[String] = executeScriptBySsh(ip, port, user, path)
  //
  //        processCapacityCalculate -= 1
  //        job.next.foreach { case (mode, ids) =>
  //
  //          ids.foreach { id =>
  //            val random_index = new Random().nextInt(formatMembers.length)
  //            val member = formatMembers(random_index)
  //            context.actorSelection(RootActorPath(member.address) / "user" / Service_Format_Script) ! ProcessMessages.WorkerJob(job.collect.name, rs, job.utcDate, job.connect.name, (mode, id))
  //          }
  //        }
  //        if (processCapacityCalculate >= 10) {
  //          println(s"CollectSshScriptWorker: ${processCapacityCalculate}")
  //        }
  //      } else {
  //        println("format service member less.")
  //      }
  //  }
}

object SshCollectWorker {
  def props(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) = Props(new SshCollectWorker(serviceMembers))
}