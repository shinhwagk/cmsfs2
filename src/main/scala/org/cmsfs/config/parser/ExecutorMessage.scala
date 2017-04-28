package org.cmsfs.config.parser

case class ExecutorMessage(collect: Collector, connector: Option[Connector], formats: Option[Seq[Format]], env: Environment)