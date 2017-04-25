import org.cmsfs.Startup
import org.cmsfs.ClusterInfo._

object TestStart {
  def main(args: Array[String]): Unit = {
    Startup.main(Seq(Service_Bootstrap, "2579", "127.0.0.1:2579").toArray)
    Thread.sleep(1000)
    Startup.main(Seq(Service_Collect_Ssh_Script, "2765", "127.0.0.1:2579").toArray)
    Thread.sleep(1000)
    Startup.main(Seq(Service_Format_Script, "2769", "127.0.0.1:2579").toArray)
    Thread.sleep(1000)
    Startup.main(Seq(Service_Collect_Jdbc, "2771", "127.0.0.1:2579").toArray)
    Thread.sleep(1000)
    Startup.main(Seq(Service_Elastic, "2770", "127.0.0.1:2579").toArray)
    Thread.sleep(1000)
    Startup.main(Seq(Service_Alarm, "2772", "127.0.0.1:2579").toArray)
  }
}
