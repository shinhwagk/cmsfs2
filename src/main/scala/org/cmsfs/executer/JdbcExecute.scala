package org.cmsfs.executer

import org.cmsfs.common.ScriptExecute
import org.cmsfs.servie.collect.jdbc.{CollectJdbcMessages, CollectingOracle}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object JdbcExecute {
  def collectFun(cr: CollectJdbcMessages.Connector, ct: CollectJdbcMessages.Collector): Option[String] = {
    val sqlText = ScriptExecute.getUrlContentByPath(ct.path)
    collectAction(cr.jdbcUrl, cr.user, cr.password, sqlText, Nil)
  }

  def collectAction(jdbcUrl: String, user: String, password: String, sqlText: String, parameters: Seq[String]): Option[String] = {
    val DBTYPE = "oracle"
    try {
      if (DBTYPE == "oracle") {
        val collectOracle = new CollectingOracle(jdbcUrl, user, password, sqlText, parameters)
        val rs = collectOracle.mode("MAP").map(Some(_))
        Await.result(rs, Duration.Inf)
      } else if (DBTYPE == "mysql") {
        None
      } else {
        None
      }
    } catch {
      case ex: Exception => None
    }
  }
}
