package org.cmsfs.config.db.table

import play.api.libs.json.Json
import slick.jdbc.MySQLProfile.api._

case class CoreCollect(id: Option[Int], name: String, mode: String, files: Seq[Seq[String]])

class CoreCollects(tag: Tag) extends Table[CoreCollect](tag, "core_collect") {

  implicit val filesColumnType: BaseColumnType[Seq[Seq[String]]] = MappedColumnType.base[Seq[Seq[String]], String](
    Json.toJson(_).toString(), Json.parse(_).as[Seq[Seq[String]]]
  )

  def id = column[Option[Int]]("ID")

  def name = column[String]("NAME")

  def mode = column[String]("MODE")

  def files = column[Seq[Seq[String]]]("FILES")

  override def * = (id, name, mode, files) <> (CoreCollect.tupled, CoreCollect.unapply)
}