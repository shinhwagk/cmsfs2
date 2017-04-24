package org.cmsfs.config.db

import slick.jdbc.MySQLProfile.api._

case class CoreConnectorSsh(id: Option[Int], name: String, ip: String, port: Int, user: String, password: Option[String], privateKey: Option[String])


class CoreConnectorSshs(tag: Tag) extends Table[CoreConnectorSsh](tag, "core_connector_ssh") {

  def id = column[Option[Int]]("ID")

  def name = column[String]("NAME")

  def ip = column[String]("IP")

  def port = column[Int]("PORT")

  def user = column[String]("USER")

  def password = column[Option[String]]("PASSWORD")

  def privateKey = column[Option[String]]("PRIVATE_KEY")

  override def * = (id, name, ip, port, user, password, privateKey) <> (CoreConnectorSsh.tupled, CoreConnectorSsh.unapply)
}
