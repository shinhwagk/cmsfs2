package org.cmsfs.config.db.table

import slick.jdbc.MySQLProfile.api._

case class CoreConnectJdbc(id: Option[Int], name: String, ip: String, port: Int, service: String, user: String, password: String)

class CoreConnectJdbcs(tag: Tag) extends Table[CoreConnectJdbc](tag, "core_connect_jdbc") {

  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def ip = column[String]("ip")

  def port = column[Int]("port")

  def service = column[String]("service")

  def user = column[String]("USER")

  def password = column[String]("PASSWORD")

  override def * = (id, name, ip, port, service, user, password) <> (CoreConnectJdbc.tupled, CoreConnectJdbc.unapply)
}