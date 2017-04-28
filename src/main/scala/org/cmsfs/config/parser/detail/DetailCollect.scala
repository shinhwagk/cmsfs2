package org.cmsfs.config.parser.detail

import play.api.libs.json.{Format, Json}

case class DetailCollect(id: Int, args: Option[String])

object DetailCollect {
  implicit val format: Format[DetailCollect] = Json.format
}