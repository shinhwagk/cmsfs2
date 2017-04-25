package org.cmsfs.servie.alarm

import akka.actor.{Actor, ActorLogging, Props}

class AlarmWorker extends Actor with ActorLogging {
  override def receive: Receive = {
    case job: AlarmMessages.WorkerJobForMail =>
      println(s"AlarmWorker mail ${job}")
    case job: AlarmMessages.WorkerJobForMobile =>
      println(s"AlarmWorker mobile ${job}")
  }
}

object AlarmWorker {
  val props = Props[AlarmWorker]
}