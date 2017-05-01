package org.cmsfs.config.db.table

import play.api.libs.json.Json
import slick.jdbc.MySQLProfile.api._

case class ConfTaskCollect(collectId: Int, connectId: Option[Int], args: Option[String])

case class ConfTaskTerminal(terminalId: Int, args: Option[String])

case class ConfTaskProcess(processId: Int, args: Option[String], terminals: Seq[ConfTaskTerminal])

case class ConfTaskSchema(collect: ConfTaskCollect, processes: Seq[ConfTaskProcess])

case class ConfTask(taskId: Option[Int], cron: String, schema: ConfTaskSchema)

class ConfTasks(tag: Tag) extends Table[ConfTask](tag, "conf_tasks") {

  implicit val filesColumnType: BaseColumnType[ConfTaskSchema] = MappedColumnType.base[ConfTaskSchema, String](
    Json.toJson(_).toString(), Json.parse(_).as[ConfTaskSchema]
  )

  def taskId = column[Option[Int]]("ID")

  def cron = column[String]("CRON")

  def schema = column[ConfTaskSchema]("SCHEMA")

  override def * = (taskId, cron, schema) <> (ConfTask.tupled, ConfTask.unapply)
}
