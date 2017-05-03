package org.cmsfs

import akka.actor.ActorSystem
import akka.cluster.Member
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.mutable
import scala.collection.mutable.Map

object Common {

  import ClusterInfo._

  private def genConfig(role: String, seed: String, port: String, config: String): Config = {
    ConfigFactory.parseString(s"akka.remote.netty.tcp.port = ${port}")
      .withFallback(ConfigFactory.parseString(s"""akka.cluster.seed-nodes = ["akka.tcp://ClusterSystem@${seed}"]"""))
      .withFallback(ConfigFactory.parseString(s"akka.cluster.roles = [${role}]"))
      .withFallback(ConfigFactory.load(config))
  }

  def genActorSystem(service: String, seed: String, port: String): ActorSystem = {
    val config = service match {
      case Service_Collect_Local =>
        genConfig(Role_Collect_Local, seed, port, Config_Collect_Script)
      case Service_Collect_Ssh =>
        genConfig(Role_Collect_Ssh, seed, port, Config_Collect_Ssh)
      case Service_Collect_Jdbc =>
        genConfig(Role_Collect_Jdbc, seed, port, Config_Collect_Jdbc)
      case Service_Bootstrap =>
        genConfig(Role_Bootstrap, seed, port, Config_Bootstrap)
      case Service_Process =>
        genConfig(Role_Process, seed, port, Config_Process)
      case Service_Service =>
        genConfig(Role_Service, seed, port, Config_Service)
    }
    ActorSystem(ClusterName, config)
  }

  def registerMember(member: Member, serviceMembers: Map[String, IndexedSeq[Member]]): Unit = {
    val role: String = member.roles.head
    val members: Option[IndexedSeq[Member]] = serviceMembers.get(member.roles.head)
    members.foreach { members =>
      val newMembers: IndexedSeq[Member] = members :+ member
      serviceMembers += (role -> newMembers)
    }
  }

  def unRegisterMember(member: Member, serviceMembers: Map[String, IndexedSeq[Member]]): Unit = {
    val role: String = member.roles.head
    val members: Option[IndexedSeq[Member]] = serviceMembers.get(member.roles.head)
    members.foreach { members =>
      val newMembers: IndexedSeq[Member] = members.filterNot(_ == member)
      serviceMembers += (role -> newMembers)
    }
  }


  def initNeedServices(serviceNames: Seq[String]): mutable.Map[String, IndexedSeq[Member]] = {
    import scala.collection.mutable.Map

    val serviceMembers: Map[String, IndexedSeq[Member]] = Map.empty

    for (name <- serviceNames) {
      serviceMembers += (name -> IndexedSeq.empty[Member])
    }
    serviceMembers
  }
}
