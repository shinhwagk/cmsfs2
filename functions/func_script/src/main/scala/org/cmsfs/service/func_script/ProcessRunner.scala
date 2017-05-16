package org.cmsfs.service.func_script

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future, blocking}
import scala.sys.process.{ProcessLogger, _}

trait ProcessRunner {
  def executeProcess(args: String*)(implicit ec: ExecutionContext): Future[String] =
    Future(blocking {
      val out = new mutable.ListBuffer[String]
      val err = new mutable.ListBuffer[String]
      val exitCode = args ! ProcessLogger(o => out += o, e => err += e)

      (exitCode, out.mkString("\n"), err.mkString("\n"))
    }).flatMap {
      case (0, stdout, _) =>
        Future.successful(stdout)
      case (code, stdout, stderr) =>
        Future.failed(ProcessRunningException(code, stdout, stderr))
    }
}

case class ProcessRunningException(exitCode: Int, stdout: String, stderr: String) extends Exception(s"code: $exitCode, stdout: $stdout, stderr: $stderr")

