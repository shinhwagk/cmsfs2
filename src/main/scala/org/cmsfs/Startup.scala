package org.cmsfs

import org.cmsfs.role.api.ApiService
import org.cmsfs.role.bootstrap.BootstrapService
import org.cmsfs.role.collect.jdbc.oracle.CollectJdbcService
import org.cmsfs.role.collect.local.CollectLocalScriptService
import org.cmsfs.role.collect.ssh.SshCollectService
import org.cmsfs.role.process.ActionService

object Startup {
  def main(args: Array[String]): Unit = {
    if (args.length >= 4) {
      runServiceMatch(args)
    } else {
      println("startup parameter should number >= 4.")
      System.exit(1)
    }
  }

  def runServiceMatch(args:Array[String]): Unit = {
    val service = args(1)
    import ClusterInfo._
    service match {
      case Service_Bootstrap =>
        BootstrapService.main(args)
      case Service_Collect_Local =>
        CollectLocalScriptService.main(args)
      case Service_Collect_Ssh =>
        SshCollectService.main(args)
      case Service_Collect_Jdbc =>
        CollectJdbcService.main(args)
      case Service_Action =>
        ActionService.main(args)
      case Service_Api =>
        ApiService.main(args)
      case _ =>
        println(s"startup service: ${service} no exist.")
        System.exit(1)
    }
  }
}
