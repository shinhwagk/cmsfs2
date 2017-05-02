package org.cmsfs.config.db

import org.cmsfs.config.db.table._
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

object QueryConfig {
  private val db = Database.forConfig("cmsfs-config")

  private val coreConnectorJdbcs = TableQuery[CoreConnectJdbcs]

  private val coreConnectorSshs = TableQuery[CoreConnectSshs]

  private val coreCollects = TableQuery[CoreCollects]

  private val coreProcesses = TableQuery[CoreProcesses]

  private val confBootstrap = TableQuery[ConfBootstraps]

  def getCoreConnectorSshById(id: Int): Future[CoreConnectSsh] = {
    db.run(coreConnectorSshs.filter(_.id === id).result.head)
  }

  def getCoreCollectById(id: Int) = {
    db.run(coreCollects.filter(_.id === id).result.head)
  }

  def getCoreConnectorJdbcById(id: Int) = {
    db.run(coreConnectorJdbcs.filter(_.id === id).result.head)
  }

  def getCoreProcessById(id: Int) = {
    db.run(coreProcesses.filter(_.id === id).result.head)
  }

  def getConfBootstrap = {
    db.run(confBootstrap.result)
  }
}
