package org.cmsfs.role.process

import org.cmsfs.common.{ScriptExecute, ScriptExecutorMode}
import play.api.libs.json.{JsValue, Json}

object Processor {

  case class ProcessConfig(files: Seq[Seq[String]], confArgsOpt: Option[JsValue])

  case class ProcessorConfig(result: String, process: ProcessConfig, env: Map[String, String])

  def executeProcess(config: ProcessorConfig): Option[String] = {
    val files: Seq[Seq[String]] = config.process.files
    val result: String = config.result
    val args: Option[JsValue] = config.process.confArgsOpt
    val env = config.env

    ScriptExecute.executeScript(files, env, Some(result), args, ScriptExecutorMode.DOWN, false)
  }

}
