package org.cmsfs.role.collect.jdbc

import akka.actor.{ActorRef, Props}
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.role.collect.{Collector, CollectorWorkerCore, CollectorWorkerMessage}

import scala.util.{Failure, Success}

class JdbcCollectWorker(collectorMasterActorRef: ActorRef) extends CollectorWorkerCore(collectorMasterActorRef) {

  log.info(s"${this.getClass.getName} start.")

  import context.dispatcher

  override def receive: Receive = {
    case CollectorWorkerMessage.WorkerJob(confTaskSchema, collectConfig, env) =>
      val connectId = confTaskSchema.collect.connect.get
      QueryConfig.getCoreConnectorJdbcById(connectId) onComplete {
        case Success(conn) => {
          implicit val newEnv: Map[String, String] =
            env + ("conn-name" -> conn.name) +
              ("conn-ip" -> conn.ip) +
              ("conn-service" -> conn.service) +
              ("conn-jdbc-category" -> conn.category)
          val collectorConfig = conn.category.toLowerCase() match {
            case "oracle" => Collector.JdbcOracleCollectorConfig(conn, collectConfig, env)
            case "mysql" => Collector.JdbcMysqlCollectorConfig(conn, collectConfig, env)
          }
          val resultOpt: Option[String] = Collector.executeCollect(collectorConfig)()
          try {
            toProcess(resultOpt, confTaskSchema.actions, newEnv)
          } catch {
            case ex: Exception => log.error(s"jdbc exec error: ${ex.getMessage}")
          }
        }
        case Failure(ex) => log.error(ex.getMessage)
      }
  }
}

object JdbcCollectWorker {
  def props(master: ActorRef) = Props(new JdbcCollectWorker(master))
}