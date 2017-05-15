package org.cmsfs.role.collect.ssh

import akka.actor.{ActorRef, Props}
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.role.collect.{Collector, CollectorWorkerCore, CollectorWorkerMessage}

import scala.util.{Failure, Success}

class SshCollectWorker(collectorMasterActorRef: ActorRef) extends CollectorWorkerCore(collectorMasterActorRef) {

  log.info(s"${this.getClass.getName} start.")

  import context.dispatcher

  override def receive: Receive = {
    case CollectorWorkerMessage.WorkerJob(task, conf, env) =>
      val connectId = task.collect.connect.get
      QueryConfig.getCoreConnectorSshById(connectId) onComplete {
        case Success(conn) => {
          try {
            implicit val newEnv: Map[String, String] = env.+("conn-name" -> conn.name).+("conn-ip" -> conn.ip)
            val collectorConfig: Collector.CollectorConfig = Collector.SshCollectorConfig(conn, conf, env)
            val resultOpt: Option[String] = Collector.executeCollect(collectorConfig)()

            toProcess(resultOpt, task.actions, newEnv)
          } catch {
            case ex: Exception => log.error(ex.getMessage)
          }
        }
        case Failure(ex) => log.error(ex.getMessage)
      }
  }
}

object SshCollectWorker {
  def props(master: ActorRef) = Props(new SshCollectWorker(master))
}