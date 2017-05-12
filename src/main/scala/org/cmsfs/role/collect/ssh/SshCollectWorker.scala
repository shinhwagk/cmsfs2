package org.cmsfs.role.collect.ssh

import akka.actor.Props
import akka.cluster.Member
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.role.collect.{Collector, CollectorWorkerCore, CollectorWorkerMessage}

import scala.collection.mutable
import scala.util.{Failure, Success}

class SshCollectWorker extends CollectorWorkerCore {

  log.info("SshCollectWorker start.")

  import context.dispatcher

  override def receive: Receive = {
    case CollectorWorkerMessage.WorkerJob(task, conf, env) =>
      val serviceActor = sender()
      val connectId = task.collect.connect.get
      QueryConfig.getCoreConnectorSshById(connectId) onComplete {
        case Success(conn) => {
          try {
            implicit val newEnv: Map[String, String] = env.+("conn-name" -> conn.name).+("conn-ip" -> conn.ip)
            val collectorConfig: Collector.CollectorConfig = Collector.SshCollectorConfig(conn, conf, env)
            val resultOpt: Option[String] = Collector.executeCollect(collectorConfig)()

            val processesOpt = task.actions

            for {
              r <- resultOpt
              p <- processesOpt
            } yield serviceActor ! (r, p,env)
          } catch {
            case ex: Exception => log.error(ex.getMessage)
          }
        }
        case Failure(ex) => log.error(ex.getMessage)
      }
  }
}

object SshCollectWorker {
  val props = Props[SshCollectWorker]
}