import org.cmsfs.Startup

object TestStart {
  def main(args: Array[String]): Unit = {
    Startup.main(Seq("bootstrap", "2579").toArray)
//    Thread.sleep(1000)
//    StartupCmsfs.main(Seq("collect-script-local", "2765").toArray)
    Thread.sleep(1000)
    Startup.main(Seq("format-script", "2769").toArray)
  }
}
