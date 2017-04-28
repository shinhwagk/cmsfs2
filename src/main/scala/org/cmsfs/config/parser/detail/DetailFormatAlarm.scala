package org.cmsfs.config.parser.detail

import play.api.libs.json.{Format, Json}

case class DetailFormatAlarm(idx: Int, threshold: String, notice: Seq[Int])

object DetailFormatAlarm {
  implicit val format: Format[DetailFormatAlarm] = Json.format
}
