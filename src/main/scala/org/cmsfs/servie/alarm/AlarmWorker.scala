package org.cmsfs.servie.alarm

import java.io.File
import java.nio.charset.Charset
import java.util.Date

import akka.actor.{Actor, ActorLogging, Props}
import org.apache.commons.io.FileUtils

class AlarmWorker extends Actor with ActorLogging {
  override def receive: Receive = {
    case job: AlarmMessages.WorkerJobForMail =>
      println(s"AlarmWorker mail ${job}")
      val content = new Date().toString + "\n" + job.subject + "\n" + job.content + "\n"
      FileUtils.writeStringToFile(new File("alarm.mail"), content, Charset.forName("UTF-8"), true)

    case job: AlarmMessages.WorkerJobForMobile =>
      println(s"AlarmWorker mobile ${job}")
      val content = new Date().toString + "\n" + job.content + "\n"
      FileUtils.writeStringToFile(new File("alarm.mobile"), content, Charset.forName("UTF-8"), true)
  }
}

object AlarmWorker {
  val props = Props[AlarmWorker]
}