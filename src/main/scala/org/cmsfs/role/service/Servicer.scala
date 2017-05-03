package org.cmsfs.role.service

import org.cmsfs.common.{ScriptExecute, ScriptExecutorMode}
import play.api.libs.json.JsValue

object Servicer {

  case class ServiceConfig(files: Seq[Seq[String]], confArgsOpt: Option[JsValue])

  case class ServicerConfig(result: String, process: ServiceConfig, env: Map[String, String])

  def executeService(config: ServicerConfig): Unit = {
    val files: Seq[Seq[String]] = config.process.files
    val result: String = config.result
    val args: Option[JsValue] = config.process.confArgsOpt
    val env = config.env

    println(files)
    println(env)
    println(args)
    ScriptExecute.executeScript(files, env, Some(result), args, ScriptExecutorMode.DOWN, false)
  }
}
