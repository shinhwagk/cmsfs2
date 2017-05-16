package org.cmsfs.role.trigger.collect.ssh

import akka.actor.ActorRef
import akka.routing.FromConfig
import org.cmsfs.role.ServiceStart
import org.cmsfs.role.trigger.collect.CollectServiceCore

class SshCollectService extends CollectServiceCore {
  override val worker: ActorRef = context.actorOf(FromConfig.props(SshCollectWorker.props(self)), "worker")
}

object SshCollectService extends ServiceStart[SshCollectService]
