package org.cmsfs.service.func_script

import play.api.libs.json.JsValue

object FunctionMessages {

  case class Function(id: Int, args: Option[JsValue], actions: Option[Seq[Function]])

  case class WorkerJob(result: String, functions: Seq[Function], env: Map[String, String])

}
