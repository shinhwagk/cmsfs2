package org.cmsfs.role.collect

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.FromConfig
import org.cmsfs.ClusterInfo._
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.config.db.table.ConfTaskSchema
import org.cmsfs.role.collect.Collector.CollectConfig

class CollectorService extends Actor with ActorLogging {

  log.info("CollectorService start.")

  import context.dispatcher

  val collectSshActor: ActorRef = context.actorOf(FromConfig.props(), name = "collect-ssh")
  val collectJdbcActor: ActorRef = context.actorOf(FromConfig.props(), name = "collect-jdbc")

  override def receive: Receive = {
    case CollectorServiceMessage.WorkerJob(task, utcDate) =>
      QueryConfig.getCoreCollectById(task.collect.id).foreach { collect =>
        val conf: CollectConfig = CollectConfig(collect.file, task.collect.args)
        val env: Map[String, String] = Map("utc-date" -> utcDate, "collect-name" -> collect.name)
        val dFun = collectServiceDistributor(task, conf, env, utcDate) _
        collect.mode match {
          case s@Service_Collect_Ssh => dFun(s, collectSshActor)
          case s@Service_Collect_Jdbc => dFun(s, collectJdbcActor)
          case _ => println("unkonws")
        }
      }
  }

  def collectServiceDistributor(task: ConfTaskSchema, conf: CollectConfig, env: Map[String, String], utcDate: String)
                               (serviceName: String, collectActor: ActorRef) = {
    collectActor ! CollectorWorkerMessage.WorkerJob(task, conf, env)
  }
}

object CollectorService {
  val props = Props[CollectorService]
}