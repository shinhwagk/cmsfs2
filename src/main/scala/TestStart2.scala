import org.cmsfs.StartupCmsfs

object TestStart2 {
  def main(args: Array[String]): Unit = {
    //    StartupCmsfs.main(Seq("bootstrap", "2573").toArray)
    //    Thread.sleep(1000)
    StartupCmsfs.main(Seq("collect-script-local", "2765").toArray)
    //    Thread.sleep(1000)
    StartupCmsfs.main(Seq("collect-script-remote", "2767").toArray)
    StartupCmsfs.main(Seq("collect-jdbc", "2768").toArray)
    //    StartupCmsfs.main(Seq("format-script", "2764").toArray)
  }
}
