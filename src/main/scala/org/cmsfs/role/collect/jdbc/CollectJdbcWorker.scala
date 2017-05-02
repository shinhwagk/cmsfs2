package org.cmsfs.role.collect.jdbc

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Member
import org.cmsfs.common.ScriptExecute
import org.cmsfs.role.collect.Collector
import org.cmsfs.role.collect.Collector.Collector
import org.cmsfs.role.collect.CollectorWorkerMessage.WorkerJob

import scala.collection.mutable
import scala.concurrent.Future

class CollectJdbcWorker(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) extends Actor with ActorLogging {

  import context.dispatcher

  override def receive: Receive = {
    case job: WorkerJob =>
//      val x: Collector = Collector.executeCollect(job.config)
//
//      val formatMembers = serviceMembers.get(Service_Format_Script).get
//      if (formatMembers.length >= 1) {
//        val f: Future[Option[String]] = collectFun(job.connect, job.collect)
//        f.foreach { rs =>
//          job.next.foreach { case (mode, ids) =>
//            ids.foreach { id =>
//              val random_index = new Random().nextInt(formatMembers.length)
//              val member = formatMembers(random_index)
//              context.actorSelection(RootActorPath(member.address) / "user" / Service_Format_Script) ! ProcessMessages.WorkerJob(job.collect.name,rs, job.utcDate, job.connect.name, (mode, id))
//            }
//          }
//        }
//      } else {
//        println("format service member less.")
//      }
  }

//  def collectFun(cr: CollectJdbcMessages.Connector, ct: CollectJdbcMessages.Collector): Future[Option[String]] = {
//    val sqlText = ScriptExecute.getUrlContentByPath(ct.path)
//    collectAction(cr.jdbcUrl, cr.user, cr.password, sqlText, Nil)
//  }

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