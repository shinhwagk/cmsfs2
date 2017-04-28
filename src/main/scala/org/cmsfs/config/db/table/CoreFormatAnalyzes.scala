package org.cmsfs.config.db.table

import slick.jdbc.MySQLProfile.api._

case class CoreFormatAnalyze(id: Option[Int], path: String, args: Option[String], _index: String, _type: String, _metric: String)

class CoreFormatAnalyzes(tag: Tag) extends Table[CoreFormatAnalyze](tag, "core_format_analyze") {

  def id = column[Option[Int]]("ID")

  def path = column[String]("PATH")

  def args = column[Option[String]]("ARGS")

  def _index = column[String]("_INDEX")

  def _type = column[String]("_TYPE")

  def _metric = column[String]("_METRIC")

  override def * = (id, path, args, _index, _type, _metric) <> (CoreFormatAnalyze.tupled, CoreFormatAnalyze.unapply)
}

