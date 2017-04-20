package org.cmsfs.servie.format

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberRemoved, MemberUp, UnreachableMember}
import com.typesafe.config.ConfigFactory
import org.cmsfs.Common
import org.cmsfs.servie.CmsfsClusterInfo
import org.cmsfs.servie.CmsfsClusterInfo.Role_Collect_Script_Local

class FormatScript extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}.", member.address, member.roles)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore
  }
}

object FormatScript {

  import CmsfsClusterInfo._

  def main(args: Array[String]): Unit = {
    val port = args(0)
    val system = Common.genActorSystem(Role_Format_Script, port)
    system.actorOf(Props[FormatScript], name = Actor_Format_Script)
  }
}