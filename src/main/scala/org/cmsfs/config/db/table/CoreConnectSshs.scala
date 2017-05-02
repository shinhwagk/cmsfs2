package org.cmsfs.config.db.table

import slick.jdbc.MySQLProfile.api._

case class CoreConnectSsh(id: Option[Int], name: String, ip: String, port: Int, user: String, password: Option[String], privateKey: Option[String])

class CoreConnectSshs(tag: Tag) extends Table[CoreConnectSsh](tag, "core_connect_ssh") {

  def id = column[Option[Int]]("ID")

  def name = column[String]("NAME")

  def ip = column[String]("IP")

  def port = column[Int]("PORT")

  def user = column[String]("USER")

  def password = column[Option[String]]("PASSWORD")

  def privateKey = column[Option[String]]("PRIVATE_KEY")

  override def * = (id, name, ip, port, user, password, privateKey) <> (CoreConnectSsh.tupled, CoreConnectSsh.unapply)
}
