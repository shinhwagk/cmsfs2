package org.cmsfs.config.db.table

import play.api.libs.json.Json
import slick.jdbc.MySQLProfile.api._

case class CoreProcess(id: Option[Int], name: String, path: String, files: Seq[String])

class CoreProcesses(tag: Tag) extends Table[CoreProcess](tag, "core_process") {

  implicit val filesColumnType: BaseColumnType[Seq[String]] =
    MappedColumnType.base[Seq[String], String](
      Json.toJson(_).toString(), Json.parse(_).as[Seq[String]])

  def id = column[Option[Int]]("ID")

  def name = column[String]("NAME")

  def path = column[String]("PATH")

  def files = column[Seq[String]]("FILES")

  override def * = (id, name, path, files) <> (CoreProcess.tupled, CoreProcess.unapply)
}