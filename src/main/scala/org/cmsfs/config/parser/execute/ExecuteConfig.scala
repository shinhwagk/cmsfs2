package org.cmsfs.config.parser.execute

case class ExecuteConfig(id: Int, name: String, path: String,
                              collect: ExecuteConfigCollect,
                              analyzes: Option[Seq[ExecuteConfigAnalyze]],
                              alarms: Option[Seq[ExecuteConfigAlarm]])