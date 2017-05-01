package org.cmsfs.servie.collect.local

import akka.actor.{Actor, ActorLogging, Props}
import org.cmsfs.servie.collect.CollectorWorkerMessage.WorkerJob

class CollectScriptLocalWorker extends Actor with ActorLogging {
  override def receive: Receive = {
    case job: WorkerJob =>
      println(s"worker local ${job}")
  }
}

object CollectScriptLocalWorker {
  val props: Props = Props[CollectScriptLocalWorker]
}