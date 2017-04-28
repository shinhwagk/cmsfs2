package org.cmsfs.config.parser.detail

import play.api.libs.json.{Format, Json}

case class DetailFormatAnalyze(idx: Int, args: Option[String])

object DetailFormatAnalyze {
  implicit val format: Format[DetailFormatAnalyze] = Json.format
}
