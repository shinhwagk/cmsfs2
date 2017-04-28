package org.cmsfs.config.parser.execute

import play.api.libs.json.{Json, Format}

case class ExecuteConfigCollect(mode: String, files: Seq[String])

object ExecuteConfigCollect {
  implicit val format: Format[ExecuteConfigCollect] = Json.format
}
