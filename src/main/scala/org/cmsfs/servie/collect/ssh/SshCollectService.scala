package org.cmsfs.servie.collect.ssh

import akka.actor.{ActorRef, Props}
import akka.routing.FromConfig
import org.cmsfs.ClusterInfo._
import org.cmsfs.Common
import org.cmsfs.servie.collect.CollectActorCore

class SshCollectService extends CollectActorCore {
  override val worker: ActorRef = context.actorOf(FromConfig.props(SshCollectWorker.props(serviceMembers)), "worker")
}

object SshCollectService {
  def main(args: Array[String]): Unit = {
    val seed = args(0)
    val port = args(1)
    val system = Common.genActorSystem(Service_Collect_Ssh, seed, port)
    system.actorOf(Props[SshCollectService], name = Actor_Collect_Ssh)
  }
}
