package org.cmsfs

object ClusterInfo {
  val ClusterName = "ClusterSystem"
  val Service_Collect_Ssh, Config_Collect_Ssh, Role_Collect_Ssh, Actor_Collect_Ssh = "collect-ssh"
  val Service_Collect_Local, Config_Collect_Script, Role_Collect_Local, Actor_Collect_Local = "collect-local"
  val Service_Collect_Jdbc, Config_Collect_Jdbc, Role_Collect_Jdbc, Actor_Collect_Jdbc = "collect-jdbc"
  val Service_Bootstrap, Config_Bootstrap, Role_Bootstrap = "bootstrap"
  val Service_Process, Config_Process, Role_Process, Actor_Process = "process"
  val Service_Terminal, Config_Terminal, Role_Terminal, Actor_Terminal = "terminal"

  case class Registration(name: String)

}