package org.cmsfs.role.collect.jdbc

import akka.actor.ActorRef
import akka.routing.FromConfig
import org.cmsfs.role.ServiceStart
import org.cmsfs.role.collect.CollectServiceCore

class JdbcCollectService extends CollectServiceCore {
  override val worker: ActorRef = context.actorOf(FromConfig.props(JdbcCollectWorker.props(self)), "worker")
}

object JdbcCollectService extends ServiceStart[JdbcCollectService]