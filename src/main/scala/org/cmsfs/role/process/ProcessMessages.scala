package org.cmsfs.role.process

import org.cmsfs.config.db.table.ConfTaskProcess
import play.api.libs.json.JsValue

object ProcessMessages {

  case class WorkerJob(confTaskProcess: ConfTaskProcess, result: String, env: Map[String, String], args: Option[JsValue])

}
