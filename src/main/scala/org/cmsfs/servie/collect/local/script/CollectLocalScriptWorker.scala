package org.cmsfs.servie.collect.local.script

import akka.actor.{Actor, ActorLogging, Props}
import org.cmsfs.servie.collect.ssh.script.CollectSshScriptMessages.WorkerJob

class CollectScriptLocalWorker extends Actor with ActorLogging {
  override def receive: Receive = {
    case a: WorkerJob =>
      println(s"worker local ${a}")
  }
}

object CollectScriptLocalWorker {
  val props: Props = Props[CollectScriptLocalWorker]
}