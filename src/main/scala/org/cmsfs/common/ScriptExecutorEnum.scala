package org.cmsfs.common

object ScriptExecutorEnum extends Enumeration {
  type ScriptExecutor = Value
  val PYTHON = Value("python")
  val SH = Value("sh")
}
