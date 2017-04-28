package org.cmsfs.config.parser

import org.cmsfs.config.db.QueryConfig
import org.cmsfs.config.parser.detail.DetailConfig
import org.cmsfs.config.parser.execute._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ExecuteParser(detail: DetailConfig) {

  lazy val config: Future[ExecuteConfig] = QueryConfig.getCoreCmsfsById(detail.configId).map { ccf =>
    val collect: ExecuteConfigCollect = Json.parse(ccf.collect).as[ExecuteConfigCollect]
    val analyze: Option[Seq[ExecuteConfigAnalyze]] = ccf.analyzes.map(a => Json.parse(a).as[Seq[ExecuteConfigAnalyze]])
    val alarm: Option[Seq[ExecuteConfigAlarm]] = ccf.analyzes.map(a => Json.parse(a).as[Seq[ExecuteConfigAlarm]])
    ExecuteConfig(ccf.id.get, ccf.name, ccf.path, collect, analyze, alarm)
  }

  val name: Future[String] = config.map(_.name)

  val mode: Future[String] = config.map(_.collect.mode)

  val rootPath: Future[String] = config.map(_.path)

  def genExecuteElem: Future[ExecutorMessage] = {
    for {
      connector <- connector
      collector <- collector
      environment <- environment
      formats <- formats
    } yield ExecutorMessage(collector, connector, formats, environment)
  }

  lazy val connector = {
    config.flatMap { config =>
      config.collect.mode match {
        case "collect-ssh-script" =>
          QueryConfig.getCoreConnectorSshById(detail.collect.id).map { conn =>
            Some(ConnectorSsh(conn.ip, conn.port, conn.user, conn.password, conn.privateKey))
          }
        case "collect-jdbc-script" =>
          QueryConfig.getCoreConnectorJdbcById(detail.collect.id).map { conn =>
            Some(ConnectorJdbc("aa", 11, "ss", conn.user, conn.password))
          }
        case "collect-local-script" =>
          Future.successful[Option[Connector]](None)
      }
    }
  }

  lazy val collector: Future[Collector] = {
    for {
      config <- config
    } yield config.collect.mode match {
      case "collect-ssh-script" =>
        CollectorSsh(config.collect.files.map(file => config.path + "/" + file), detail.collect.args)
      case "collect-jdbc_script" =>
        CollectorJdbc(config.collect.files.map(file => config.path + "/" + file), detail.collect.args)
    }
  }

  lazy val environment: Future[Environment] = {
    for {
      config <- config
    } yield config.collect.mode match {
      case "collect-ssh-script" =>
        EnvironmentSsh(1, config.name)
      case "collect-jdbc_script" =>
        EnvironmentJdbc()
    }
  }

  lazy val formats: Future[Option[Seq[Format]]] = {
    for {
      analyzes <- analyzes
      alarms <- alarms
    } yield {
      for {
        als <- analyzes
        ars <- alarms
      } yield als ++ ars
    }
  }

  lazy val analyzes: Future[Option[Seq[Format]]] = {
    for {
      config <- config
      rootPath <- rootPath
    } yield {
      for {
        da <- detail.analyzes
        ca <- config.analyzes
      } yield da.flatMap { d =>
        val analyze: ExecuteConfigAnalyze = ca(d.idx)
        analyze.files.map(file => FormatAnalyze(rootPath + "/" + file, d.args, analyze._index, analyze._type))
      }

    }
  }

  lazy val alarms: Future[Option[Seq[Format]]] = {
    for {
      config <- config
      rootPath <- rootPath
    } yield {
      for {
        da <- detail.alarms
        ca <- config.alarms
      } yield da.flatMap { d =>
        val analyze: ExecuteConfigAlarm = ca(d.idx)
        analyze.files.map(file => FormatAlarm(rootPath + "/" + file, d.threshold, Seq(1)))
      }
    }
  }
}
