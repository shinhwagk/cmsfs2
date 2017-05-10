package org.cmsfs.config.db.table

import play.api.libs.json.{JsValue, Json}
import slick.jdbc.MySQLProfile.api._

case class CoreService(id: Option[Int], name: String, files: Seq[Seq[String]], args: Option[JsValue])

class CoreServices(tag: Tag) extends Table[CoreService](tag, "core_service") {

  implicit val filesColumnType: BaseColumnType[Seq[Seq[String]]] =
    MappedColumnType.base[Seq[Seq[String]], String](Json.toJson(_).toString(), Json.parse(_).as[Seq[Seq[String]]])

  implicit val argsColumnType: BaseColumnType[JsValue] =
    MappedColumnType.base[JsValue, String](Json.toJson(_).toString(), Json.parse(_))

  def id = column[Option[Int]]("ID")

  def name = column[String]("NAME")

  def files = column[Seq[Seq[String]]]("FILES")

  def args = column[Option[JsValue]]("ARGS")

  override def * = (id, name, files, args) <> (CoreService.tupled, CoreService.unapply)
}