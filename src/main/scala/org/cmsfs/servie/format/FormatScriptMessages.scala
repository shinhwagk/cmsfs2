package org.cmsfs.servie.format

object FormatScriptMessages {

  case class WorkerJob(result: Option[String],utcDate:String,dslName:String, next: (String, Int))

}
