package org.cmsfs.executer

import org.cmsfs.config.db.QueryConfig
import org.cmsfs.config.db.table.CoreCmsfsDetail
import org.cmsfs.config.parser.detail.{DetailConfig, DetailCollect, DetailFormatAlarm, DetailFormatAnalyze}
import org.cmsfs.config.parser.ExecuteParser
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global

object test {
  def main(args: Array[String]): Unit = {
    while (true) {
      val start = System.currentTimeMillis()
      QueryConfig.getCoreCmsfsDetails.foreach { ccd =>
        val row: CoreCmsfsDetail = ccd(0)
        val collect: DetailCollect = Json.parse(row.collect).as[DetailCollect]
        val analyze = row.analyzes.map(Json.parse(_).as[Seq[DetailFormatAnalyze]])
        val alarms = row.alarms.map(Json.parse(_).as[Seq[DetailFormatAlarm]])
        val x = DetailConfig(row.id.get, row.cron, row.cmsfsId, collect, analyze, alarms)
        new ExecuteParser(x).genExecuteElem.foreach(x => println(System.currentTimeMillis() - start, x))
      }
      Thread.sleep(1000)
    }
  }


}
