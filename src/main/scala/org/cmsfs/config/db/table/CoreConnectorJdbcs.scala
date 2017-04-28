package org.cmsfs.config.db.table

import slick.jdbc.MySQLProfile.api._

case class CoreConnectorJdbc(id: Option[Int], name: String, url: String, user: String, password: String)

class CoreConnectorJdbcs(tag: Tag) extends Table[CoreConnectorJdbc](tag, "core_connector_jdbc") {

  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def url = column[String]("URL")

  def user = column[String]("USER")

  def password = column[String]("PASSWORD")

  override def * = (id, name, url, user, password) <> (CoreConnectorJdbc.tupled, CoreConnectorJdbc.unapply)
}