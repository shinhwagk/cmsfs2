package org.cmsfs

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

object Common {

  private def genConfig(cluster: String, role: String, seed: String, port: String, serviceSelfConfig: String): Config = {
    ConfigFactory.parseString(s"akka.remote.netty.tcp.port = ${port}")
      .withFallback(ConfigFactory.parseString(s"""akka.cluster.seed-nodes = ["akka.tcp://${cluster}@${seed}"]"""))
      .withFallback(ConfigFactory.parseString(s"akka.cluster.roles = [${role}]"))
      .withFallback(ConfigFactory.load(serviceSelfConfig))
  }

  def genActorSystem(args: Array[String]): ActorSystem = {
    val Array(cluster, service, port, seed) = args
    val role = service
    val config = genConfig(cluster, role, seed, port, service)
    ActorSystem(cluster, config)
  }
}
