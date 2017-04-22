package org.cmsfs

import org.cmsfs.servie.bootstrap.BootstrapActor
import org.cmsfs.servie.collect.jdbc.CollectJdbcService
import org.cmsfs.servie.collect.local.script.CollectScriptLocalService
import org.cmsfs.servie.collect.ssh.script.CollectScriptRemote
import org.cmsfs.servie.format.FormatScript

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
      case Service_Collect_Script_Local =>
        CollectScriptLocalService.main(args)
      case Service_Collect_Script_Remote =>
        CollectScriptRemote.main(args)
      case Service_Collect_Jdbc =>
        CollectJdbcService.main(args)
      case Service_Bootstrap =>
        BootstrapActor.main(args)
      case Service_Format_Script =>
        FormatScript.main(args)
      case _ =>
        println(s"startup service: ${service} no exist.")
        System.exit(1)
    }
  }
}
