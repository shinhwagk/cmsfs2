package org.cmsfs.servie.collect.script.remote

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.RoundRobinPool
import org.cmsfs.servie.collect.script.remote.CollectScriptRemoteMessages.WorkerJob

class CollectScriptRemoteWorker extends Actor with ActorLogging {
  override def receive: Receive = {
    case a: WorkerJob => println(s"worker Remote ${a}")
  }
}

object CollectScriptRemoteWorker {
  val props = Props[CollectScriptRemoteWorker]
}