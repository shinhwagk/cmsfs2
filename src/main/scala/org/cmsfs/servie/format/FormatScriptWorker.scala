package org.cmsfs.servie.format

import akka.actor.{Actor, ActorLogging, Props}
import org.cmsfs.servie.format.FormatScriptMessages.WorkerJob

class FormatScriptWorker extends Actor with ActorLogging {
  println("FormatScriptWorker start.")

  override def receive: Receive = {
    case job: WorkerJob =>
      println("FormatScriptWorker", job)
  }
}

object FormatScriptWorker {
  val props = Props[FormatScriptWorker]
}