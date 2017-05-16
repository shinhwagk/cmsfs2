package org.cmsfs.functions

import akka.actor.{Actor, ActorLogging, Props}

class FunctionsService extends Actor with ActorLogging {
  override def receive: Receive = ???
}

object FunctionsService {
  val props = Props[FunctionsService]
}