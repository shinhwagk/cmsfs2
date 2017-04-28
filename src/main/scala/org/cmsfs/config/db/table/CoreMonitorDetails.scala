package org.cmsfs.config.db.table

import play.api.libs.json.Json
import slick.jdbc.MySQLProfile.api._

case class CoreMonitorDetail(id: Option[Int], cron: String,
                             connectorMode: String, connectorIds: Seq[Int],
                             collectId: Int, collectArgs: Option[String],
                             formatAnalyzeId: Option[Int], formatAnalyzeArgs: Option[String],
                             formatAlarmIds: Seq[Int])


class CoreMonitorDetails(tag: Tag) extends Table[CoreMonitorDetail](tag, "core_monitor_detail") {
  implicit val seqIntColumnType = MappedColumnType.base[Seq[Int], String](
    { b => Json.toJson(b).toString() }, { i => Json.parse(i).as[Seq[Int]] }
  )

  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

  def cron = column[String]("CRON")

  def connectorMode = column[String]("CONNECTOR_MODE")

  def connectorIds = column[Seq[Int]]("CONNECTOR_IDS")

  def collectId = column[Int]("COLLECT_ID")

  def collectArgs = column[Option[String]]("COLLECT_ARGS")

  def formatAnalyzeId = column[Option[Int]]("FORMAT_ANALYZE_ID")

  def formatAnalyzeArgs = column[Option[String]]("FORMAT_ANALYZE_ARGS")

  def formatAlarmIds = column[Seq[Int]]("FORMAT_ALARM_IDS")

  override def * = (id, cron, connectorMode, connectorIds, collectId, collectArgs, formatAnalyzeId, formatAnalyzeArgs, formatAlarmIds) <> (CoreMonitorDetail.tupled, CoreMonitorDetail.unapply)
}