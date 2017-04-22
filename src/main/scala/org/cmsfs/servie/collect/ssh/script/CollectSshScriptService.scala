package org.cmsfs.servie.collect.ssh.script

import akka.actor.{ActorRef, Props}
import akka.routing.FromConfig
import org.cmsfs.ClusterInfo._
import org.cmsfs.Common
import org.cmsfs.servie.collect.CollectActorCore

class CollectScriptRemote extends CollectActorCore {
  override val worker: ActorRef = context.actorOf(FromConfig.props(CollectSshScriptWorker.props), "worker")
}

object CollectScriptRemote {
  def main(args: Array[String]): Unit = {
    val seed = args(0)
    val port = args(1)
    val system = Common.genActorSystem(Service_Collect_Script_Remote, seed, port)
    system.actorOf(Props[CollectScriptRemote], name = Actor_Collect_Script_Remote)
  }
}
