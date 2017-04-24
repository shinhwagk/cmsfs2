package org.cmsfs.servie.format

object FormatScriptMessages {

  case class Format(path: String, args: Option[String], next: String)

  case class WorkerJob(result: String, format: Format)
}
