package org.cmsfs.servie.collect.script.local

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.RoundRobinPool
import org.cmsfs.servie.collect.script.local.CollectScriptLocalMessages.WorkerJob

class CollectScriptLocalWorker extends Actor with ActorLogging {
  override def receive: Receive = {
    case a: WorkerJob =>
      println(s"worker local ${a}")
  }
}

object CollectScriptLocalWorker {
  def props(routerNumber: Int) = RoundRobinPool(routerNumber).props(Props[CollectScriptLocalWorker])
}