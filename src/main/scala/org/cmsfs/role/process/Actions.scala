package org.cmsfs.role.process

import play.api.libs.json.JsValue

case class WorkerAction(collectResult: String, evn: Map[String, String], action: Action)

case class Action(id: Int, args: Option[JsValue], actions: Option[Seq[Action]])