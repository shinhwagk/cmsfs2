package org.cmsfs.servie.process

import org.cmsfs.common.{ScriptExecute, ScriptExecutorMode}
import org.cmsfs.servie.collect.Collector.CollectorEnv
import play.api.libs.json.{JsArray, Json}

object Processor {

  case class ProcessConfig(path: String, files: Seq[String], args: Option[String])

  case class ProcessorConfig(result: String, process: ProcessConfig, env: CollectorEnv)

  def executeProcess(config: ProcessorConfig): Seq[String] = {
    val path = config.process.path
    val result = config.result
    val args = config.process.args
    val env = Json.toJson(config.env).toString()
    //    val path = config.process.files
    val processResult: String = ScriptExecute.executeScript(path, Some(result), args, env, ScriptExecutorMode.DOWN, false)
    Json.parse(processResult).as[Seq[String]]
  }
}
