package org.cmsfs.servie.collect.local.script

import akka.actor.{ActorRef, Props}
import akka.routing.FromConfig
import org.cmsfs.ClusterInfo._
import org.cmsfs.Common
import org.cmsfs.servie.collect.CollectActorCore

class CollectLocalScriptService extends CollectActorCore {
  override val worker: ActorRef = context.actorOf(FromConfig.props(CollectScriptLocalWorker.props), "worker")
}

object CollectLocalScriptService {
  def main(args: Array[String]): Unit = {
    val seed = args(0)
    val port = args(1)
    val system = Common.genActorSystem(Service_Collect_Local_Script, seed, port)
    system.actorOf(Props[CollectLocalScriptService], name = Actor_Collect_Script_Local)
  }
}
