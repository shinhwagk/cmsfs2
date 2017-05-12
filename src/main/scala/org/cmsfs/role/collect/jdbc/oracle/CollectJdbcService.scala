package org.cmsfs.role.collect.jdbc.oracle

import akka.actor.ActorRef
import akka.routing.FromConfig
import org.cmsfs.role.ServiceStart
import org.cmsfs.role.collect.CollectServiceCore
import org.cmsfs.role.collect.jdbc.CollectJdbcWorker

class CollectJdbcService extends CollectServiceCore {
  override val worker: ActorRef = context.actorOf(FromConfig.props(CollectJdbcWorker.props), "worker")
}

object CollectJdbcService extends ServiceStart[CollectJdbcWorker]