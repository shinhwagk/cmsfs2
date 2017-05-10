package org.cmsfs.role.collect

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.ClusterInfo._
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.config.db.table.ConfTaskSchema
import org.cmsfs.role.collect.Collector.CollectConfig

import scala.collection.mutable
import scala.util.Random

class CollectorService(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) extends Actor with ActorLogging {

  log.info("CollectorService start.")

  import context.dispatcher

  override def receive: Receive = {
    case CollectorServiceMessage.WorkerJob(task, utcDate) =>
      QueryConfig.getCoreCollectById(task.collect.id).foreach { collect =>
        val conf: CollectConfig = CollectConfig(collect.file, task.collect.args)
        val env: Map[String, String] = Map("utc-date" -> utcDate, "collect-name" -> collect.name)
        val dFun = collectServiceDistributor(task, conf, env, utcDate) _
        collect.mode match {
          case s@Service_Collect_Ssh => dFun(s)
          case s@Service_Collect_Jdbc => dFun(s)
          case _ => println("unkonws")
        }
      }
  }

  def collectServiceDistributor(task: ConfTaskSchema, conf: CollectConfig, env: Map[String, String], utcDate: String)(serviceName: String) = {
    val workers: IndexedSeq[Member] = serviceMembers.get(serviceName).get
    if (workers.length >= 1) {
      val worker: Member = workers(new Random().nextInt(workers.length))
      context.actorSelection(RootActorPath(worker.address) / "user" / serviceName) !
        CollectorWorkerMessage.WorkerJob(task, conf, env)
    } else {
      log.error(s"collect service ${Service_Collect_Ssh} less.")
    }
  }
}

object CollectorService {
  def props(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) = Props(new CollectorService(serviceMembers))
}