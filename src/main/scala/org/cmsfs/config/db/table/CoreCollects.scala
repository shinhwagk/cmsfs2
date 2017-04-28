package org.cmsfs.config.db.table

import slick.jdbc.MySQLProfile.api._

case class CoreCollect(id: Option[Int], path: String, name: String, args: Option[String])

class CoreCollects(tag: Tag) extends Table[CoreCollect](tag, "core_collect") {

  def id = column[Option[Int]]("ID")

  def path = column[String]("PATH")

  def name = column[String]("NAME")

  def args = column[Option[String]]("ARGS")

  override def * = (id, path, name, args) <> (CoreCollect.tupled, CoreCollect.unapply)
}