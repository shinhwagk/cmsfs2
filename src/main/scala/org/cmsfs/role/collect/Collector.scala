package org.cmsfs.role.collect

import org.cmsfs.common.ScriptExecute
import org.cmsfs.config.db.table.{CoreConnectJdbc, CoreConnectSsh}
import org.cmsfs.role.collect.jdbc.oracle.{CollectingMysql, CollectingOracle}
import org.cmsfs.role.collect.ssh.SshCollectWorkerAction

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object Collector extends
  SshCollectWorkerAction {

  case class CollectConfig(file: Seq[String], args: Option[String])

  sealed trait CollectorConfig

  abstract class JdbcCollectorConfig extends CollectorConfig {
    val connect: CoreConnectJdbc
    val collect: CollectConfig
  }

  case class JdbcOracleCollectorConfig(connect: CoreConnectJdbc, collect: CollectConfig, env: Map[String, String])
    extends JdbcCollectorConfig

  case class JdbcMysqlCollectorConfig(connect: CoreConnectJdbc, collect: CollectConfig, env: Map[String, String])
    extends JdbcCollectorConfig

  case class SshCollectorConfig(connect: CoreConnectSsh, collect: CollectConfig, env: Map[String, String])
    extends CollectorConfig

  case class LocalCollectorConfig(collect: CollectConfig, env: Map[String, String])
    extends CollectorConfig

  type Collector = () => Option[String]

  private def sshCollector(config: SshCollectorConfig): Collector = {
    val ip = config.connect.ip
    val port = config.connect.port
    val user = config.connect.user
    val file = config.collect.file
    () => executeScriptBySsh(ip, port, user, file)
  }

  private def genJdbcUrl(category: String, ip: String, port: Int, service: String): String = {
    category.toLowerCase match {
      case "oracle" => s"jdbc:oracle:thin:@//${ip}:${port}/${service}"
      case "mysql" => s"jdbc:mysql://${ip}:${port}/${service}?useSSL=false"
      case _=> throw new Exception(s"jdbc database type ${category} no Implement.")
    }
  }

  private def jdbcCollector(jcc: JdbcCollectorConfig): Collector = {
    val category = jcc.connect.category
    val ip = jcc.connect.ip
    val port = jcc.connect.port
    val service = jcc.connect.service
    val user = jcc.connect.user
    val password = jcc.connect.password
    val jdbcUrl = genJdbcUrl(category, ip, port, service)
    val sqlText = ScriptExecute.getUrlContentByPath(jcc.collect.file)
    jcc match {
      case d: JdbcMysqlCollectorConfig =>
        () => Some(Await.result((new CollectingMysql(jdbcUrl, user, password, sqlText, Seq())).mode("MAP"), Duration.Inf))
      case d: JdbcOracleCollectorConfig =>
        () => Some(Await.result((new CollectingOracle(jdbcUrl, user, password, sqlText, Seq())).mode("MAP"), Duration.Inf))
    }
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
