package org.cmsfs

object ClusterInfo {
  val ClusterName = "CMSFSSystem"
  val Service_Collect_Ssh_Script, Config_Collect_Ssh_Script, Role_Collect_Ssh_Script, Actor_Collect_Ssh_Script = "collect-ssh"
  val Service_Collect_Local_Script, Config_Collect_Script_Local, Role_Collect_Script_Local, Actor_Collect_Local_Script = "collect-local"
  val Service_Collect_Jdbc, Config_Collect_Jdbc, Role_Collect_Jdbc, Actor_Collect_Jdbc = "collect-jdbc"
  val Service_Bootstrap, Config_Bootstrap, Role_Bootstrap = "bootstrap"
  val Service_Process, Config_Process, Role_Process, Actor_Process = "process"

  case class Registration(name: String)

}