package org.cmsfs.servie.format

object FormatScriptMessages {

  case class WorkerJob(collectName:String,result: Option[String], utcDate: String, dslName: String, next: (String, Int))

}
