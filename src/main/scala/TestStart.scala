import org.cmsfs.StartupCmsfs

object TestStart {
  def main(args: Array[String]): Unit = {
    StartupCmsfs.main(Seq("bootstrap", "2562").toArray)
    Thread.sleep(1000)
    StartupCmsfs.main(Seq("collect-script-local", "2563").toArray)
    Thread.sleep(1000)
    StartupCmsfs.main(Seq("format-script", "2564").toArray)
  }
}
