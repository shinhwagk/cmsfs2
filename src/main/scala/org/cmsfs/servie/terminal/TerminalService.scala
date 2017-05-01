package org.cmsfs.servie.terminal

import akka.actor.{Actor, ActorLogging}
import org.cmsfs.servie.terminal.TerminalMessages.WorkerJob

class TerminalService extends Actor with ActorLogging {
  override def receive: Receive = {
    case x@WorkerJob(confTaskService, processResult, env) =>
      println(x)
  }
}
