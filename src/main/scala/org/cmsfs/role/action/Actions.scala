package org.cmsfs.role.action

import play.api.libs.json.JsValue

case class WorkerAction(data: String, action: Action, env: Map[String, String])

case class Action(id: Int, args: Option[JsValue], actions: Option[Seq[Action]])