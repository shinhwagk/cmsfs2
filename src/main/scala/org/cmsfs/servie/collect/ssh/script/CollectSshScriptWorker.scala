package org.cmsfs.servie.collect.ssh.script

import akka.actor.{Actor, ActorLogging, Props}
import org.cmsfs.servie.collect.ssh.script.CollectSshScriptMessages.WorkerJob

class CollectSshScriptWorker extends Actor with ActorLogging {
  override def receive: Receive = {
    case a: WorkerJob => println(s"worker Remote ${a}")
  }
}

object CollectSshScriptWorker {
  val props = Props[CollectSshScriptWorker]
}