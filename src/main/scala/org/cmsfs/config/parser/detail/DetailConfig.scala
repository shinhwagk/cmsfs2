package org.cmsfs.config.parser.detail

case class DetailConfig(id: Int, cron: String, configId: Int,
                        collect: DetailCollect,
                        analyzes: Option[Seq[DetailFormatAnalyze]],
                        alarms: Option[Seq[DetailFormatAlarm]])
