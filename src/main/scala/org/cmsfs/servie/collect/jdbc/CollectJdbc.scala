package org.cmsfs.servie.collect.jdbc

import akka.actor.ActorRef
import org.cmsfs.servie.collect.CollectActorCore

class CollectJdbc extends CollectActorCore {
  override val worker: ActorRef = ???
}

object CollectJdbc {
  def main(args: Array[String]): Unit = {

  }
}
