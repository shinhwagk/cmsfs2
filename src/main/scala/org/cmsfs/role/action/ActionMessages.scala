package org.cmsfs.role.action

import org.cmsfs.config.db.table.ConfTaskAction
import play.api.libs.json.JsValue

object ActionMessages {

  case class WorkerJob(collectResult: String, action: Seq[ConfTaskAction], env: Map[String, String])

}
