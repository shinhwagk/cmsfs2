import org.cmsfs.bootstrap.BootstrapEnd
import org.cmsfs.collect.script.CollectScriptEnd

object Start extends App {
  CollectScriptEnd.main(Seq("2562").toArray)
  Thread.sleep(1000)
  BootstrapEnd.main(Seq("2561").toArray)
  Thread.sleep(2000)
  CollectScriptEnd.main(Seq("2563").toArray)
}
