package org.cmsfs.role.collect

import org.cmsfs.config.db.table.{CoreConnectJdbc, CoreConnectSsh}
import org.cmsfs.role.collect.ssh.SshCollectWorkerAction
import play.api.libs.json.{Format, Json}

object Collector extends
  SshCollectWorkerAction {

  case class CollectConfig(files: Seq[Seq[String]], args: Option[String])

  case class CollectorEnv(utcDate: String, collectName: String, connectName: Option[String] = None)

  object CollectorEnv {
    implicit val format: Format[CollectorEnv] = Json.format
  }

  sealed trait CollectorConfig

  case class JdbcCollectorConfig(connect: CoreConnectJdbc, collect: CollectConfig, env: CollectorEnv)
    extends CollectorConfig

  case class SshCollectorConfig(connect: CoreConnectSsh, collect: CollectConfig, env: CollectorEnv)
    extends CollectorConfig

  case class LocalCollectorConfig(collect: CollectConfig, env: CollectorEnv)
    extends CollectorConfig

  type Collector = () => Option[String]

  private def sshCollector(config: SshCollectorConfig): Collector = {
    val ip = config.connect.ip
    val port = config.connect.port
    val user = config.connect.user
    val file = config.collect.files(0)
    () => executeScriptBySsh(ip, port, user, file)
  }

  private def jdbcCollector(config: JdbcCollectorConfig): Collector = {
    val sqlFile = config.collect.files(0)
    () => executeScriptBySsh("", 11, "", Seq(""))
  }

  private def localCollector(config: LocalCollectorConfig): Collector = {
    () => Some("")
  }

  def executeCollect(config: CollectorConfig): Collector = {
    config match {
      case c: JdbcCollectorConfig => jdbcCollector(c)
      case c: SshCollectorConfig => sshCollector(c)
      case c: LocalCollectorConfig => localCollector(c)
    }
  }
}