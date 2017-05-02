package org.cmsfs.config.db.table

import play.api.libs.json.{Format, JsValue, Json}
import slick.jdbc.MySQLProfile.api._

case class ConfTaskCollect(id: Int, connect: Option[Int], args: Option[String])

object ConfTaskCollect {
  implicit val format: Format[ConfTaskCollect] = Json.format
}

case class ConfTaskProcess(id: Int, args: Option[JsValue])

object ConfTaskProcess {
  implicit val format: Format[ConfTaskProcess] = Json.format
}

case class ConfTaskSchema(collect: ConfTaskCollect, processes: Seq[ConfTaskProcess])

object ConfTaskSchema {
  implicit val format: Format[ConfTaskSchema] = Json.format
}

case class ConfBootstrap(bootstrapId: Option[Int], cron: String, schema: ConfTaskSchema)

class ConfBootstraps(tag: Tag) extends Table[ConfBootstrap](tag, "conf_bootstrap") {

  implicit val filesColumnType: BaseColumnType[ConfTaskSchema] = MappedColumnType.base[ConfTaskSchema, String](
    Json.toJson(_).toString(), Json.parse(_).as[ConfTaskSchema]
  )

  def bootstrapId = column[Option[Int]]("ID")

  def cron = column[String]("CRON")

  def schema = column[ConfTaskSchema]("SCHEMA")

  override def * = (bootstrapId, cron, schema) <> (ConfBootstrap.tupled, ConfBootstrap.unapply)
}
