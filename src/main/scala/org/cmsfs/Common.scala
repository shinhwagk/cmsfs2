package org.cmsfs

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import org.cmsfs.servie.CmsfsClusterInfo

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
      case Service_Bootstrap =>
        genConfig(Role_Bootstrap, port, Config_Bootstrap)
      case Service_Format_Script =>
        genConfig(Role_Format_Script, port, Config_Format_Script)
    }
    ActorSystem(ClusterName, config)
  }
}
