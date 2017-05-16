package org.cmsfs.role.trigger.collect.local

import akka.actor.{ActorRef, Props}
import akka.routing.FromConfig
import org.cmsfs.ClusterInfo._
import org.cmsfs.Common
import org.cmsfs.role.ServiceStart
import org.cmsfs.role.trigger.collect.CollectServiceCore

class CollectLocalScriptService extends CollectServiceCore {
  override val worker: ActorRef = context.actorOf(FromConfig.props(CollectScriptLocalWorker.props), "worker")
}

object CollectLocalScriptService extends ServiceStart[CollectLocalScriptService]