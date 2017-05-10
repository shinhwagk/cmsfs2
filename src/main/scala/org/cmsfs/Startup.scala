package org.cmsfs

import org.cmsfs.role.bootstrap.BootstrapService
import org.cmsfs.role.collect.jdbc.oracle.CollectJdbcService
import org.cmsfs.role.collect.local.CollectLocalScriptService
import org.cmsfs.role.collect.ssh.SshCollectService
import org.cmsfs.role.process.ProcessService
import org.cmsfs.role.service.ServiceMaster

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
      case Service_Collect_Local =>
        CollectLocalScriptService.main(args)
      case Service_Collect_Ssh =>
        SshCollectService.main(args)
      case Service_Collect_Jdbc =>
        CollectJdbcService.main(args)
      case Service_Process =>
        ProcessService.main(args)
      case Service_Service =>
        ServiceMaster.main(args)
      case _ =>
        println(s"startup service: ${service} no exist.")
        System.exit(1)
    }
  }
}
