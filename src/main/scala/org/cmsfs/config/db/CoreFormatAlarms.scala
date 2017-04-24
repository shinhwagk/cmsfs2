package org.cmsfs.config.db

import play.api.libs.json.{Format, Json}
import slick.jdbc.MySQLProfile.api._

case class CoreNotification(mails: Seq[String], mobiles: Seq[String])

object CoreNotification {
  implicit val format: Format[CoreNotification] = Json.format
}

case class CoreFormatAlarm(id: Option[Int], path: String,  args: String, notification: CoreNotification)

class CoreFormatAlarms(tag: Tag) extends Table[CoreFormatAlarm](tag, "core_format_alarm") {

  implicit val notificationIntColumnType = MappedColumnType.base[CoreNotification, String](
    { b => Json.toJson(b).toString() }, { i => Json.parse(i).as[CoreNotification] }
  )

  def id = column[Option[Int]]("ID")

  def path = column[String]("PATH")

  def args = column[String]("ARGS")

  def notification = column[CoreNotification]("NOTIFICATION")

  override def * = (id, path, args, notification) <> (CoreFormatAlarm.tupled, CoreFormatAlarm.unapply)
}