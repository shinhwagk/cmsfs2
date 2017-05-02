package org.cmsfs.role.process

import org.cmsfs.common.{ScriptExecute, ScriptExecutorMode}
import org.cmsfs.role.collect.Collector.CollectorEnv
import play.api.libs.json.{JsValue, Json}

object Processor {

  case class ProcessConfig(files: Seq[Seq[String]], args: Option[JsValue])

  case class ProcessorConfig(result: String, process: ProcessConfig, env: CollectorEnv)

  def executeProcess(config: ProcessorConfig): Unit = {
    val files = config.process.files
    val result = config.result
    val args = config.process.args
    val env = Json.toJson(config.env).toString()
    println(files)
    println(result)
    println(args)
//    val processResult: String = ScriptExecute.executeScript(files, env, Some(result), args, ScriptExecutorMode.DOWN, false)
//    Json.parse(processResult).as[Seq[String]]
  }
}
