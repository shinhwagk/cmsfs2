package org.cmsfs.config.db.table

import slick.jdbc.MySQLProfile.api._

case class CoreCmsfsDetail(id: Option[Int], cron: String, cmsfsId: Int, collect: String, analyzes: Option[String], alarms: Option[String])

class CoreCmsfsDetails(tag: Tag) extends Table[CoreCmsfsDetail](tag, "core_cmsfs_details") {

  def id = column[Option[Int]]("ID")

  def core = column[String]("CRON")

  def cmsfsId = column[Int]("CMSFS_ID")

  def collect = column[String]("COLLECT")

  def analyzes = column[Option[String]]("ANALYZES")

  def alarms = column[Option[String]]("ALARMS")

  override def * = (id, core, cmsfsId, collect, analyzes, alarms) <> (CoreCmsfsDetail.tupled, CoreCmsfsDetail.unapply)
}
