package org.cmsfs.servie.collect.ssh.script

import akka.actor.{ActorRef, Props}
import akka.routing.FromConfig
import org.cmsfs.ClusterInfo._
import org.cmsfs.Common
import org.cmsfs.servie.collect.CollectActorCore

class CollectSshScriptService extends CollectActorCore {
  val formatMember = serviceMembers.get(Service_Format_Script).get
  override val worker: ActorRef = context.actorOf(FromConfig.props(CollectSshScriptWorker.props(formatMember)), "worker")
}

object CollectSshScriptService {
  def main(args: Array[String]): Unit = {
    val seed = args(0)
    val port = args(1)
    val system = Common.genActorSystem(Service_Collect_Ssh_Script, seed, port)
    system.actorOf(Props[CollectSshScriptService], name = Actor_Collect_Ssh_Script)
  }
}
