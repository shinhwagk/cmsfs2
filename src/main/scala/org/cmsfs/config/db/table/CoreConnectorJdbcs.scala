package org.cmsfs.config.db.table

import slick.jdbc.MySQLProfile.api._

case class CoreConnectorJdbc(id: Option[Int], name: String, ip: String, port: Int, service: String, user: String, password: String)

class CoreConnectorJdbcs(tag: Tag) extends Table[CoreConnectorJdbc](tag, "core_connector_jdbc") {

  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def ip = column[String]("ip")

  def port = column[Int]("port")

  def service = column[String]("service")

  def user = column[String]("USER")

  def password = column[String]("PASSWORD")

  override def * = (id, name, ip, port, service, user, password) <> (CoreConnectorJdbc.tupled, CoreConnectorJdbc.unapply)
}