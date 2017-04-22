package org.cmsfs.servie.collect.jdbc

import akka.actor.{ActorRef, Props}
import akka.routing.FromConfig
import org.cmsfs.Common
import org.cmsfs.ClusterInfo.{Actor_Collect_Jdbc, Service_Collect_Jdbc}
import org.cmsfs.servie.collect.CollectActorCore

class CollectJdbcService extends CollectActorCore {
  override val worker: ActorRef = context.actorOf(FromConfig.props(CollectJdbcWorker.props), "worker")
}

object CollectJdbcService {
  def main(args: Array[String]): Unit = {
    val seed = args(0)
    val port = args(1)
    val system = Common.genActorSystem(Service_Collect_Jdbc, seed, port)
    system.actorOf(Props[CollectJdbcService], name = Actor_Collect_Jdbc)
  }
}
