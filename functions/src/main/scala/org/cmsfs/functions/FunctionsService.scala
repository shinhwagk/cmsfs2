package org.cmsfs.functions

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberUp, UnreachableMember}

class FunctionsServiceController extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  var functionServices: Map[String, Int] = Map.empty

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}.", member.address, member.roles)
      val roleName = member.roles.head
      if (roleName.startsWith("func")) {
        val roleCntOpt = functionServices.get(roleName)
        if (roleCntOpt.isDefined) {
          functionServices += (roleName -> (roleCntOpt.get + 1))
        } else {
          functionServices += (roleName -> 1)
        }
      }
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
      val roleName = member.roles.head
      if (roleName.startsWith("func")) {
        val roleCnt = functionServices.get(roleName).get
        if (roleCnt == 1) {
          functionServices -= member.roles.head
        } else {
          functionServices += (roleName -> (roleCnt - 1))
        }
      }
  }
}

object FunctionsServiceController {
  val props = Props[FunctionsServiceController]
}

case class ProcessUnit(result: String, files: Seq[Seq[String]], env: Map[String, String])