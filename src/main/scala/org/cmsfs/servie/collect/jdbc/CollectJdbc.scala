package org.cmsfs.servie.collect.jdbc

import akka.actor.{ActorRef, Props}
import akka.routing.FromConfig
import org.cmsfs.Common
import org.cmsfs.ClusterInfo.{Actor_Collect_Jdbc, Service_Collect_Jdbc}
import org.cmsfs.servie.collect.CollectActorCore

class CollectJdbc extends CollectActorCore {
  override val worker: ActorRef = context.actorOf(FromConfig.props(CollectJdbcWorker.props), "worker")
}

object CollectJdbc {
  def main(args: Array[String]): Unit = {
    val port = args(0)
    val system = Common.genActorSystem(Service_Collect_Jdbc, port)
    system.actorOf(Props[CollectJdbc], name = Actor_Collect_Jdbc)
  }
}
