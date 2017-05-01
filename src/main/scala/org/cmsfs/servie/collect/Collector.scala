package org.cmsfs.servie.collect

import org.cmsfs.servie.collect.ssh.SshCollectWorkerAction
import play.api.libs.json.{Format, Json}

object Collector extends
  SshCollectWorkerAction {

  case class CollectConfig(path: String, files: Seq[String], args: Option[String])

  case class CollectorEnv(utcDate: String,collectName:String, connectName: Option[String]=None)

  object CollectorEnv {
    implicit val format: Format[CollectorEnv] = Json.format
  }

  sealed trait CollectorConfig

  case class JdbcCollectorConfig(connect: JdbcConnect, collect: CollectConfig, env: CollectorEnv)
    extends CollectorConfig

  case class SshCollectorConfig(connect: SshConnect, collect: CollectConfig, env: CollectorEnv)
    extends CollectorConfig

  case class LocalCollectorConfig(collect: CollectConfig, env: CollectorEnv)
    extends CollectorConfig

  type Collector = () => String

  private def sshCollector(config: SshCollectorConfig): Collector = {
    () => executeScriptBySsh("", 11, "", "").get
  }

  private def jdbcCollector(config: JdbcCollectorConfig): Collector = {
    () => executeScriptBySsh("", 11, "", "").get
  }

  private def localCollector(config: LocalCollectorConfig): Collector = {
    () => ""
  }

  def executeCollect(config: CollectorConfig): Collector = {
    config match {
      case c: JdbcCollectorConfig => jdbcCollector(c)
      case c: SshCollectorConfig => sshCollector(c)
      case c: LocalCollectorConfig => localCollector(c)
    }
  }
}
