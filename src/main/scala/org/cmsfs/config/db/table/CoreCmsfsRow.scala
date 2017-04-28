package org.cmsfs.config.db.table

import slick.jdbc.MySQLProfile.api._

case class Collect(mode: String, files: Seq[String])

case class Analyze(files: Seq[String], _index: String, _type: String)

case class Alarm(files: Seq[String])

case class CoreCmsfsRow(id: Option[Int], name: String, path: String, collect: String, alarms: Option[String], analyzes: Option[String])

class CoreCmsfs(tag: Tag) extends Table[CoreCmsfsRow](tag, "core_cmsfs") {

  def id = column[Option[Int]]("ID")

  def name = column[String]("NAME")

  def path = column[String]("PATH")

  def collect = column[String]("COLLECT")

  def analyzes = column[Option[String]]("ANALYZES")

  def alarms = column[Option[String]]("ALARMS")

  override def * = (id, name, path, collect, alarms, analyzes) <> (CoreCmsfsRow.tupled, CoreCmsfsRow.unapply)
}