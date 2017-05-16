package org.cmsfs.role.trigger.collect.jdbc

import akka.actor.{ActorRef, Props}
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.role.trigger.collect.{Collector, CollectorWorkerCore, CollectorWorkerMessage}

import scala.util.{Failure, Success}

class JdbcCollectWorker(collectorMasterActorRef: ActorRef) extends CollectorWorkerCore(collectorMasterActorRef) {

  log.info(s"${this.getClass.getName} start.")

  import context.dispatcher

  override def receive: Receive = {
    case CollectorWorkerMessage.WorkerJob(confTaskSchema, collectConfig, env) =>
      val connectId = confTaskSchema.collect.connect.get
      QueryConfig.getCoreConnectorJdbcById(connectId) onComplete {
        case Success(conn) => {
          val newEnv: Map[String, String] =
            env + ("conn-name" -> conn.name) +
              ("conn-ip" -> conn.ip) +
              ("conn-service" -> conn.service) +
              ("conn-jdbc-category" -> conn.category)

          val resultOpt: Option[String] = conn.category.toLowerCase() match {
            case "oracle" =>
              val collectorConfig: Collector.CollectorConfig = Collector.JdbcOracleCollectorConfig(conn, collectConfig, env)
              Collector.executeCollect(collectorConfig)()
            case "mysql" =>
              val collectorConfig: Collector.CollectorConfig = Collector.JdbcMysqlCollectorConfig(conn, collectConfig, env)
              Collector.executeCollect(collectorConfig)()
          }

          toProcess(resultOpt, confTaskSchema.actions, newEnv)
        }
        case Failure(ex) => log.error(ex.getMessage)
      }
  }

  override val xxx: ActorRef = context.parent
}

object JdbcCollectWorker {
  def props(master: ActorRef) = Props(new JdbcCollectWorker(master))
}