package org.cmsfs.service.func_script

import akka.actor.{Actor, ActorLogging, Props}

class FunctionProcessWorker extends Actor with ActorLogging with ProcessRunner {
  override def receive: Receive = {
    case _: String => executeProcess(Seq("x"): _*).recover { case xc => xc.getMessage; None }
      sender() ! "some"
  }
}

object FunctionProcessWorker {
  val props = Props[FunctionProcessWorker]
}