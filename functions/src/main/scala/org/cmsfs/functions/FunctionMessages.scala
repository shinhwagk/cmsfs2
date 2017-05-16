package org.cmsfs.functions

object FunctionMessages {

  case class Action(id: Int, args: Option[JsValue], actions: Option[Seq[Action]])

  case class WorkerJob(collectResult: String, action: Seq[Action], env: Map[String, String])

}
