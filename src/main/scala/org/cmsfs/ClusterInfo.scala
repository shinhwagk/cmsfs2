package org.cmsfs

object ClusterInfo {
  val ClusterName = "ClusterSystem"
  val Service_Collect_Ssh_Script, Config_Collect_Ssh_Script, Role_Collect_Ssh_Script, Actor_Collect_Ssh_Script = "collect-ssh-script"
  val Service_Collect_Local_Script, Config_Collect_Script_Local, Role_Collect_Script_Local, Actor_Collect_Script_Local = "collect-script-local"
  val Service_Collect_Jdbc, Config_Collect_Jdbc, Role_Collect_Jdbc, Actor_Collect_Jdbc = "collect-jdbc"
  val Service_Bootstrap, Config_Bootstrap, Role_Bootstrap = "bootstrap"
  val Service_Format_Script, Config_Format_Script, Role_Format_Script, Actor_Format_Script = "format-script"

  case class Registration(name: String)

}