package org.cmsfs

import akka.actor.{Actor, ActorSystem, Props}
import akka.cluster.Member
import com.typesafe.config.{Config, ConfigFactory}
import org.cmsfs.servie.CmsfsClusterInfo
import org.cmsfs.servie.collect.script.local.CollectScriptLocalWorker

import scala.collection.mutable
import scala.collection.mutable.Map

object Common {

  import CmsfsClusterInfo._

  private def genConfig(role: String, port: String, config: String): Config = {
    ConfigFactory.parseString(s"akka.remote.netty.tcp.port = ${port}")
      .withFallback(ConfigFactory.parseString(s"akka.cluster.roles = [${role}]"))
      .withFallback(ConfigFactory.load(config))
  }

  def genActorSystem(service: String, port: String): ActorSystem = {
    val config = service match {
      case Service_Collect_Script_Local =>
        genConfig(Role_Collect_Script_Local, port, Config_Collect_Script_Local)
      case Service_Collect_Script_Remote =>
        genConfig(Role_Collect_Script_Remote, port, Config_Collect_Script_Remote)
      case Service_Collect_Jdbc =>
        genConfig(Role_Collect_Jdbc, port, Config_Collect_Jdbc)
      case Service_Bootstrap =>
        genConfig(Role_Bootstrap, port, Config_Bootstrap)
      case Service_Format_Script =>
        genConfig(Role_Format_Script, port, Config_Format_Script)
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
