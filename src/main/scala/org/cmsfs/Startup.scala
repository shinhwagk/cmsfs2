package org.cmsfs

import org.cmsfs.servie.alarm.AlarmService
import org.cmsfs.servie.bootstrap.BootstrapService
import org.cmsfs.servie.collect.jdbc.CollectJdbcService
import org.cmsfs.servie.collect.local.script.CollectLocalScriptService
import org.cmsfs.servie.collect.ssh.script.CollectSshScriptService
import org.cmsfs.servie.elasticsearch.ElasticSearchService
import org.cmsfs.servie.format.FormatScriptService

object Startup {
  def main(args: Array[String]): Unit = {
    if (args.length >= 2) {
      val service = args(0)
      val port = args(1)
      val seed = args(2)
      runServiceMatch(service, port, seed)
    } else {
      println("startup parameter should number >= 2.")
      System.exit(1)
    }
  }

  def runServiceMatch(service: String, port: String, seed: String): Unit = {
    val args = Seq(seed, port).toArray
    import ClusterInfo._
    service match {
      case Service_Bootstrap =>
        BootstrapService.main(args)
      case Service_Collect_Local_Script =>
        CollectLocalScriptService.main(args)
      case Service_Collect_Ssh_Script =>
        CollectSshScriptService.main(args)
      case Service_Collect_Jdbc =>
        CollectJdbcService.main(args)
      case Service_Format_Script =>
        FormatScriptService.main(args)
      case Service_Elastic =>
        ElasticSearchService.main(args)
      case Service_Alarm =>
        AlarmService.main(args)
      case _ =>
        println(s"startup service: ${service} no exist.")
        System.exit(1)
    }
  }
}
