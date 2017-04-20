import org.cmsfs.Startup

object TestStart2 {
  def main(args: Array[String]): Unit = {
    //    StartupCmsfs.main(Seq("bootstrap", "2573").toArray)
    //    Thread.sleep(1000)
    Startup.main(Seq("collect-script-local", "2785").toArray)
    //    Thread.sleep(1000)
    Startup.main(Seq("collect-script-remote", "2787").toArray)
    Startup.main(Seq("collect-jdbc", "2788").toArray)
    //    StartupCmsfs.main(Seq("format-script", "2764").toArray)
  }
}
