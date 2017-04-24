package org.cmsfs.servie.collect.ssh.script

import java.io.{BufferedReader, InputStreamReader}

import com.jcraft.jsch.{ChannelExec, JSch}
import org.cmsfs.common.ScriptExecute
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.Json

import scala.collection.mutable.ArrayBuffer

trait CollectSshScriptWorkerAction {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def executeScriptBySsh(ip: String, port: Int, user: String, path: String): Option[String] = {
    collectAction(ip, user, ScriptExecute.getUrlByPath(path), port)
  }

  def collectAction(host: String, user: String, scriptUrl: String, port: Int = 22): Option[String] = {
    val OSName = System.getProperty("os.name").toLowerCase();
    try {
      if (OSName.startsWith("win")) {
        // test
        Some(ssh("C:\\Users\\zhangxu\\.ssh\\id_rsa", user, host, scriptUrl, port));
      } else if (OSName == "linux") {
        Some("xxxx")
        //        Some(ssh("~/.ssh/id_rsa", user, host, scriptUrl, port));
      } else {
        logger.error("OS not match..");
        None
      }
    } catch {
      case ex: Exception => {
        println(ex.getMessage + s" host :${host}" + " collectionAction");
        logger.error(ex.getMessage + s" host :${host}" + " collectionAction");
        None
      }
    }
  }

  def ssh(keyPath: String, user: String, host: String, scriptUrl: String, port: Int): String = {
    val jsch = new JSch();
    jsch.addIdentity(keyPath);
    val session = jsch.getSession(user, host, port);
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();

    val channelExec: ChannelExec = session.openChannel("exec").asInstanceOf[ChannelExec]
    val in = channelExec.getInputStream();
    channelExec.setCommand(s"curl -sk ${scriptUrl} | sh");
    channelExec.connect();

    val reader = new BufferedReader(new InputStreamReader(in));

    val rs = new ArrayBuffer[String]()

    var line: Option[String] = Option(reader.readLine())

    while (line.isDefined) {
      rs += line.get
      line = Option(reader.readLine())
    }

    val exitStatus: Int = channelExec.getExitStatus();

    channelExec.disconnect();
    session.disconnect();

    logger.info(s"stage $exitStatus")

    //    if (exitStatus < 0) {
    //      logger.info(s"stage $exitStatus")
    //      // System.out.println("Done, but exit status not set!");
    //    } else if (exitStatus > 0) {
    //      // System.out.println("Done, but with error!");
    //    } else {
    //      // System.out.println("Done!");
    //    }
    Json.toJson(rs).toString()
  }
}
