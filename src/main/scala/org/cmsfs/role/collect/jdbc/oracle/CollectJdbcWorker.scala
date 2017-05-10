package org.cmsfs.role.collect.jdbc

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.ClusterInfo.Actor_Process
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.role.collect.jdbc.oracle.CollectingOracle
import org.cmsfs.role.collect.{Collector, CollectorWorkerMessage}
import org.cmsfs.role.process.ProcessMessages

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

class CollectJdbcWorker(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) extends Actor with ActorLogging {

  import context.dispatcher

  override def receive: Receive = {
    case CollectorWorkerMessage.WorkerJob(confTaskSchema, collectConfig, env) =>
      val connectId = confTaskSchema.collect.connect.get

      QueryConfig.getCoreConnectorJdbcById(connectId) onComplete {
        case Success(conn) => {
          val newEnv: Map[String, String] = env.+("conn-name" -> conn.name).+("conn-ip" -> conn.ip).+("conn-service" -> conn.service)


          val resultOpt: Option[String] = conn.category.toLowerCase() match {
            case "oracle" =>
              val collectorConfig: Collector.CollectorConfig = Collector.JdbcOracleCollectorConfig(conn, collectConfig, env)
              Collector.executeCollect(collectorConfig)()
            case "mysql" =>
              val collectorConfig: Collector.CollectorConfig = Collector.JdbcMysqlCollectorConfig(conn, collectConfig, env)
              Collector.executeCollect(collectorConfig)()
          }

          resultOpt foreach { result =>
            confTaskSchema.processes.foreach { process =>
              val members: IndexedSeq[Member] = serviceMembers.get(Actor_Process).get
              if (members.length >= 1) {
                val member: Member = members(new Random().nextInt(members.length))
                context.actorSelection(RootActorPath(member.address) / "user" / Actor_Process) !
                  ProcessMessages.WorkerJob(process, result, newEnv, process.args)
              } else {
                log.error("process role less.")
              }
            }
          }
        }
        case Failure(ex) => log.error(ex.getMessage)
      }
  }

  def collectAction(jdbcUrl: String, user: String, password: String, sqlText: String, parameters: Seq[String]): Future[Option[String]] = {
    val DBTYPE = "oracle"
    try {
      if (DBTYPE == "oracle") {
        val collectOracle = new CollectingOracle(jdbcUrl, user, password, sqlText, parameters)
        collectOracle.mode("MAP").map(Some(_))
      } else if (DBTYPE == "mysql") {
        Future.successful(None)
      } else {
        log.error("DBTYPE not match..");
        Future.successful(None)
      }
    } catch {
      case ex: Exception => log.error(ex.getMessage + " collectionAction"); Future.successful(None)
    }
  }
}

object CollectJdbcWorker {
  def props(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) = Props(new CollectJdbcWorker(serviceMembers))
}