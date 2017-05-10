package org.cmsfs.config.db.table

import play.api.libs.json.Json
import slick.jdbc.MySQLProfile.api._

case class CoreProcess(id: Option[Int], collectId: Int, name: String, files: Seq[Seq[String]])

class CoreProcesses(tag: Tag) extends Table[CoreProcess](tag, "core_process") {

  implicit val filesColumnType: BaseColumnType[Seq[Seq[String]]] =
    MappedColumnType.base[Seq[Seq[String]], String](Json.toJson(_).toString(), Json.parse(_).as[Seq[Seq[String]]])

  def id = column[Option[Int]]("ID")

  def collectId = column[Int]("COLLECT_ID")

  def name = column[String]("NAME")

  def files = column[Seq[Seq[String]]]("FILES")

  override def * = (id, collectId, name, files) <> (CoreProcess.tupled, CoreProcess.unapply)
}