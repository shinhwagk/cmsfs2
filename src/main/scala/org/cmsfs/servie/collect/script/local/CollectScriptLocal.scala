package org.cmsfs.servie.collect.script.local

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.typesafe.config.ConfigFactory
import org.cmsfs.Common
import org.cmsfs.servie.CmsfsClusterInfo

import scala.util.Random

class CollectScriptLocal extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  //  val a = context.actorOf(Props[ABC],name="collect-script")
  //
  //  println(a.path)

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

object CollectScriptLocal {

  import CmsfsClusterInfo._

  def main(args: Array[String]): Unit = {
    val port = args(0)
    val system = Common.genActorSystem(Service_Collect_Script_Local, port)
    val collectScript = system.actorOf(Props[CollectScriptLocal], name = Role_Collect_Script_Local)
  }
}
