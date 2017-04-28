package org.cmsfs.config.parser.execute

import play.api.libs.json.{Format, Json}

case class ExecuteConfigAnalyze(files: Seq[String], _index: String, _type: String)

object ExecuteConfigAnalyze {
  implicit val format: Format[ExecuteConfigAnalyze] = Json.format
}

case class ExecuteConfigAlarm(files: Seq[String])

object ExecuteConfigAlarm {
  implicit val format: Format[ExecuteConfigAlarm] = Json.format
}
