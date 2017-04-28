package org.cmsfs.servie.format

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.ClusterInfo._
import org.cmsfs.common.{ScriptExecute, ScriptExecutorMode}
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.servie.alarm.AlarmMessages
import org.cmsfs.servie.elasticsearch.ElasticSearchMessage
import org.cmsfs.servie.format.FormatScriptMessages.WorkerJob
import play.api.libs.json.{Format, JsArray, JsValue, Json}

import scala.collection.mutable
import scala.util.Random

class FormatScriptWorker(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) extends Actor with ActorLogging {
  println("FormatScriptWorker start.")

  import context.dispatcher

  override def receive: Receive = {
    case job: WorkerJob =>
      val id = job.next._2
      val mode = job.next._1
      mode match {
        case "alarm" =>
          QueryConfig.getCoreFormatAlarmsById(id).foreach { fa =>
            try {
              val result = ScriptExecute.executeScript(fa.path, job.result, Some(fa.args), ScriptExecutorMode.DOWN, false)
              val alarmContent = Json.parse(result).as[Seq[AAAA]]

              val members = serviceMembers.get(Service_Alarm).get
              if (members.length >= 1) {
                fa.notification.mails.foreach { mail =>
                  val random_index = new Random().nextInt(members.length)
                  val member = members(random_index)
                  alarmContent.foreach { ac =>
                    ac.mail.foreach(m =>
                      context.actorSelection(RootActorPath(member.address) / "user" / Service_Alarm) ! AlarmMessages.WorkerJobForMail(mail, job.collectName + "/" + job.dslName, m))
                  }
                }
                fa.notification.mobiles.foreach { mobile =>
                  val random_index = new Random().nextInt(members.length)
                  val member = members(random_index)
                  alarmContent.foreach { ac =>
                    ac.mobile.foreach(m =>
                      context.actorSelection(RootActorPath(member.address) / "user" / Service_Alarm) ! AlarmMessages.WorkerJobForMobile(mobile, m)
                    )
                  }
                }
              } else {
                println("format service member less.")
              }
            } catch {
              case ex: Exception =>
                log.error(ex.getMessage)
            }
          }
        case "elastic" =>
          QueryConfig.getCoreFormatAnalyzesById(id).foreach { fa =>
            try {
              val result: String = ScriptExecute.executeScript(fa.path, job.result, None, ScriptExecutorMode.DOWN, false)
              val arr: Seq[JsValue] = Json.parse(result).as[JsArray].value
              arr.foreach { rs =>
                val members = serviceMembers.get(Service_Elastic).get
                if (members.length >= 1) {
                  val random_index = new Random().nextInt(members.length)
                  val member = members(random_index)
                  val metaData = ElasticSearchMessage.MetaData(fa._index, job.dslName, fa._metric, job.utcDate, job.dslName)
                  //                val metaData = ElasticSearchMessage.MetaData(fa._index, fa._type, fa._metric, job.utcDate,"")
                  context.actorSelection(RootActorPath(member.address) / "user" / Service_Elastic) ! ElasticSearchMessage.WorkerJob(rs.toString(), metaData)
                } else {
                  println("format service member less.")
                }
              }
            } catch {
              case ex: Exception =>
                log.error(s"dslName:${job.dslName}, result: ${job.result}, ${ex.getMessage}")
            }
          }
      }
  }
}

object FormatScriptWorker {
  def props(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) = Props(new FormatScriptWorker(serviceMembers))
}

case class AAAA(mail: Option[String], mobile: Option[String])

object AAAA {
  implicit val format: Format[AAAA] = Json.format
}