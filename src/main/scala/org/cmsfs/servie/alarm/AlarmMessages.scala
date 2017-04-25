package org.cmsfs.servie.alarm

object AlarmMessages {

  trait WorkerJob

  case class WorkerJobForMail(mail: String, subject: String, content: String) extends WorkerJob

  case class WorkerJobForMobile(mobile: String, content: String) extends WorkerJob

}
