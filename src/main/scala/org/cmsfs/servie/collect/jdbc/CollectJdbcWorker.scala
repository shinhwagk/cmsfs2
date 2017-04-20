package org.cmsfs.servie.collect.jdbc

import akka.actor.{Actor, ActorLogging, Props}
import org.cmsfs.servie.collect.jdbc.CollectJdbcMessages.WorkerJob

class CollectJdbcWorker extends Actor with ActorLogging {
  override def receive: Receive = {
    case job: WorkerJob =>
      println(s"worker jdbc ${job.name}")
  }
}

object CollectJdbcWorker {
  val props = Props[CollectJdbcWorker]
}