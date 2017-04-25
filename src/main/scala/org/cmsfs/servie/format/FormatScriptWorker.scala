package org.cmsfs.servie.format

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.ClusterInfo._
import org.cmsfs.common.{ScriptExecute, ScriptExecutorMode}
import org.cmsfs.config.QueryConfig
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
            val result: String = ScriptExecute.executeScript(fa.path, job.result, Some(fa.args), ScriptExecutorMode.DOWN, false)
            val alarmContent = Json.parse(result).as[AAAA]
            val members = serviceMembers.get(Service_Alarm).get
            if (members.length >= 1) {
              fa.notification.mails.foreach { mail =>
                val random_index = new Random().nextInt(members.length)
                val member = members(random_index)
                context.actorSelection(RootActorPath(member.address) / "user" / Service_Alarm) ! AlarmMessages.WorkerJobForMail(mail, job.dslName, alarmContent.mail)
              }
              fa.notification.mobiles.foreach { mobile =>
                val random_index = new Random().nextInt(members.length)
                val member = members(random_index)
                context.actorSelection(RootActorPath(member.address) / "user" / Service_Alarm) ! AlarmMessages.WorkerJobForMobile(mobile, alarmContent.mobile)
              }
            } else {
              println("format service member less.")
            }
          }
        case "elastic" =>
          QueryConfig.getCoreFormatAnalyzesById(id).foreach { fa =>
            val result: String = ScriptExecute.executeScript(fa.path, job.result, None, ScriptExecutorMode.DOWN, false)

            val arr: Seq[JsValue] = Json.parse(result).as[JsArray].value
            arr.foreach { rs =>
              val members = serviceMembers.get(Service_Elastic).get
              if (members.length >= 1) {
                val random_index = new Random().nextInt(members.length)
                val member = members(random_index)
                val metaData = ElasticSearchMessage.MetaData(fa._index, job.dslName, fa._metric, job.utcDate, "")
                //                val metaData = ElasticSearchMessage.MetaData(fa._index, fa._type, fa._metric, job.utcDate,"")
                context.actorSelection(RootActorPath(member.address) / "user" / Service_Elastic) ! ElasticSearchMessage.WorkerJob(rs.toString(), metaData)
              } else {
                println("format service member less.")
              }
            }

          }
      }
  }
}

object FormatScriptWorker {
  def props(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) = Props(new FormatScriptWorker(serviceMembers))
}

case class AAAA(mail: String, mobile: String)

object AAAA {
  implicit val format: Format[AAAA] = Json.format
}