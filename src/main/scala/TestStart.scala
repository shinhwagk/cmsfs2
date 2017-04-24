import org.cmsfs.Startup

object TestStart {
  def main(args: Array[String]): Unit = {
    Startup.main(Seq("bootstrap", "2579", "127.0.0.1:2579").toArray)
    Thread.sleep(1000)
    Startup.main(Seq("collect-ssh-script", "2765", "127.0.0.1:2579").toArray)
    Thread.sleep(1000)
    Startup.main(Seq("format-script", "2769", "127.0.0.1:2579").toArray)
  }
}
