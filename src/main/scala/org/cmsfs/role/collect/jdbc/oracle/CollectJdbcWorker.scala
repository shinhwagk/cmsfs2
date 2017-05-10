package org.cmsfs.role.collect.jdbc

import akka.actor.Props
import akka.cluster.Member
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.role.collect.{Collector, CollectorWorkerCore, CollectorWorkerMessage}

import scala.collection.mutable
import scala.util.{Failure, Success}

class CollectJdbcWorker(serviceMembers: mutable.Map[String, IndexedSeq[Member]])
  extends CollectorWorkerCore(serviceMembers) {

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

          val resultOpt: Option[String] = conn.category.toLowerCase() match {
            case "oracle" =>
              val collectorConfig: Collector.CollectorConfig = Collector.JdbcOracleCollectorConfig(conn, collectConfig, env)
              Collector.executeCollect(collectorConfig)()
            case "mysql" =>
              val collectorConfig: Collector.CollectorConfig = Collector.JdbcMysqlCollectorConfig(conn, collectConfig, env)
              Collector.executeCollect(collectorConfig)()
          }

          val processesOpt = confTaskSchema.actions

          toProcess(resultOpt, processesOpt)
        }
        case Failure(ex) => log.error(ex.getMessage)
      }
  }
}

object CollectJdbcWorker {
  def props(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) = Props(new CollectJdbcWorker(serviceMembers))
}