package org.cmsfs.role.process

import org.cmsfs.config.db.table.ConfTaskAction
import play.api.libs.json.JsValue

object ProcessMessages {

  case class WorkerJob(confTaskProcess: ConfTaskAction, result: String, env: Map[String, String], args: Option[JsValue])

}
