package org.cmsfs.config

import org.cmsfs.config.db._
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

object QueryConfig {
  private val db = Database.forConfig("cmsfs-config")

  private val coreMonitorDetails = TableQuery[CoreMonitorDetails]

  private val coreConnectorJdbcs = TableQuery[CoreConnectorJdbcs]

  private val coreConnectorSshs = TableQuery[CoreConnectorSshs]

  private val coreCollects = TableQuery[CoreCollects]

  private val coreFormatAlarms = TableQuery[CoreFormatAlarms]

  private val coreFormatAnalyzes = TableQuery[CoreFormatAnalyzes]

  def getCoreMonitorDetails: Future[Seq[CoreMonitorDetail]] = {
    db.run(coreMonitorDetails.result)
  }

  def getCoreConnectorSshById(id: Int): Future[CoreConnectorSsh] = {
    db.run(coreConnectorSshs.filter(_.id === id).result.head)
  }

  def getCoreCollectById(id: Int) = {
    db.run(coreCollects.filter(_.id === id).result.head)
  }

  def getCoreMonitorDetailById(id: Int) = {
    db.run(coreMonitorDetails.filter(_.id === id).result.head)
  }

  def getCoreFormatAnalyzesById(id: Int) = {
    db.run(coreFormatAnalyzes.filter(_.id === id).result.head)
  }

  def getCoreFormatAlarmsById(id: Int) = {
    db.run(coreFormatAlarms.filter(_.id === id).result.head)
  }

  def getCoreConnectorJdbcById(id: Int) = {
    db.run(coreConnectorJdbcs.filter(_.id === id).result.head)
  }

}
