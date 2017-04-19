package org.cmsfs.servie.collect.script.remote

import akka.actor.{ActorRef, Props}
import org.cmsfs.Common
import org.cmsfs.servie.CmsfsClusterInfo._
import org.cmsfs.servie.collect.CollectActorCore

class CollectScriptRemote extends CollectActorCore {
  override val worker: ActorRef = context.actorOf(CollectScriptRemoteWorker.props(10), "worker")
}

object CollectScriptRemote {
  def main(args: Array[String]): Unit = {
    val port = args(0)
    val system = Common.genActorSystem(Service_Collect_Script_Remote, port)
    system.actorOf(Props[CollectScriptRemote], name = Actor_Collect_Script_Remote)
  }
}
