package org.cmsfs.config.db.table

import play.api.libs.json.Json
import slick.jdbc.MySQLProfile.api._

case class CoreCollect(id: Option[Int], name: String, mode: String, path: String, files: Seq[String])

class CoreCollects(tag: Tag) extends Table[CoreCollect](tag, "core_collect") {

  implicit val filesColumnType: BaseColumnType[Seq[String]] = MappedColumnType.base[Seq[String], String](
    Json.toJson(_).toString(), Json.parse(_).as[Seq[String]]
  )

  def id = column[Option[Int]]("ID")

  def name = column[String]("NAME")

  def mode = column[String]("MODE")

  def path = column[String]("PATH")

  def files = column[Seq[String]]("FILES")

  override def * = (id, name, mode, path, files) <> (CoreCollect.tupled, CoreCollect.unapply)
}