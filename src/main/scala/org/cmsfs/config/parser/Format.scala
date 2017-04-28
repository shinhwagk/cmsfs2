package org.cmsfs.config.parser

sealed trait Format

case class FormatAnalyze(path: String, args: Option[String], _index: String, _type: String) extends Format

case class FormatAlarm(path: String, threshold: String, notice: Seq[Int]) extends Format
