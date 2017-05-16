package org.cmsfs.role.trigger.collect.local

import akka.actor.{Actor, ActorLogging, Props}
import org.cmsfs.role.trigger.collect.CollectorWorkerMessage.WorkerJob

class CollectScriptLocalWorker extends Actor with ActorLogging {
  override def receive: Receive = {
    case job: WorkerJob =>
      println(s"worker local ${job}")
  }
}

object CollectScriptLocalWorker {
  val props: Props = Props[CollectScriptLocalWorker]
}