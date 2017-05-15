import java.io.File

import org.apache.commons.io.FileUtils
import org.cmsfs.Startup
import org.cmsfs.ClusterInfo._

object TestStart {
  def main(args: Array[String]): Unit = {
    val workspace = new File("workspace")
    FileUtils.deleteDirectory(workspace)
    FileUtils.forceMkdir(workspace)

    val clusterName = "ClusterSystem"
    Startup.main(Seq(clusterName, Service_Bootstrap, "2579", "127.0.0.1:2579").toArray)
    Thread.sleep(1000)
    Startup.main(Seq(clusterName, Service_Collect_Ssh, "2765", "127.0.0.1:2579").toArray)
    Thread.sleep(1000)
    Startup.main(Seq(clusterName, Service_Collect_Jdbc, "2766", "127.0.0.1:2579").toArray)
    Thread.sleep(1000)
    Startup.main(Seq(clusterName, Service_Action, "2769", "127.0.0.1:2579").toArray)
    Thread.sleep(1000)
    Startup.main(Seq(clusterName, Service_Api, "2777", "127.0.0.1:2579").toArray)
  }
}
