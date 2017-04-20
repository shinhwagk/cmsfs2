package org.cmsfs.common

import java.io.File
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.ThreadLocalRandom

import org.apache.commons.io.FileUtils
import org.cmsfs.common.ScriptExecutorMode.ScriptExecuteMode
import play.api.libs.json.Json

import scala.io.Source

object ScriptExecute {

  private def getUrlByPath(path: String): String = {
    Json.parse(path).as[List[String]] mkString "/"
  }

  private def getUrlContentByPath(path: String): String = {
    Source.fromURL(getUrlByPath(path), "UTF-8").mkString.trim
  }

  private def createWorkDirAndDeleteAfterOperation(): (String, () => Unit) = {
    val dirName = s"workspace/${ThreadLocalRandom.current.nextLong(100000000).toString}"
    val dir = new File(dirName)
    FileUtils.forceMkdir(dir)

    (dirName, () => FileUtils.deleteDirectory(dir))
  }

  private def writeData(data: String, dirPath: String): Unit =
    writeFile(s"${dirPath}/data.json", data)

  private def writeArgs(args: String, dirPath: String): Unit =
    writeFile(s"${dirPath}/args.json", args)

  private def downScript(url: String, dirPath: String): String = {
    val scriptName = url.split("/").last
    FileUtils.copyURLToFile(new URL(url), new File(dirPath + "/" + scriptName))
    scriptName
  }

  private def writeFile(fileName: String, content: String): Unit =
    FileUtils.writeStringToFile(new File(fileName), content, Charset.forName("UTF-8"), false)

  private def executorChoice(url: String, executeMode: ScriptExecuteMode): Seq[String] = {
    val name = url.split(".").last
    if (ScriptExecutorMode.ONLINE == executeMode) {
      name match {
        case "py" => Seq("python")
        case "sh" => Seq("sh")
        case "ps1" => Seq("powershell")
        case _ => throw new Exception(s"actuator unknown: ${name}")
      }
    } else {
      name match {
        case "py" => Seq("python", "-")
        case "sh" => Seq("sh", "-")
        case _ => throw new Exception(s"actuator unknown: ${name}")
      }
    }
  }

  def executeScript(path: String, data: Option[String], args: Option[String], executeMode: ScriptExecuteMode, resultToArrayByLine: Boolean): String = {
    val url = getUrlByPath(path)
    val rs = executeMode match {
      case ScriptExecutorMode.ONLINE =>
        executeScriptForOnline(url, data, args)
      case ScriptExecutorMode.DOWN =>
        executeScriptForDown(url, data, args)
    }
    if (resultToArrayByLine) rs.split("\n").map(_.trim).mkString("[\"", "\",\"", "\"]") else rs.trim
  }

  private def executeScriptForOnline(url: String, data: Option[String], args: Option[String]): String = {
    import sys.process._
    val executor: Seq[String] = executorChoice(url, ScriptExecutorMode.ONLINE)
    if (data.isDefined) {
      args match {
        case Some(args) =>
          val argsSeq: Seq[String] = Json.parse(args).as[Seq[String]]
          Seq("curl", "-sk", url) #| (executor ++ argsSeq) !!
        case None =>
          Seq("curl", "-sk", url) #| executor !!
      }
    } else {
      executeScriptForDown(url, data, args)
    }
  }

  private def executeScriptForDown(url: String, dataOpt: Option[String], argsOpt: Option[String]): String = {
    import sys.process._
    val executor: Seq[String] = executorChoice(url, ScriptExecutorMode.DOWN)
    val (workDirName, deleteWorkDirFun) = createWorkDirAndDeleteAfterOperation()
    val scriptName = downScript(url, workDirName)
    dataOpt.foreach(data => writeData(data, workDirName))
    argsOpt.foreach(args => writeData(args, workDirName))
    val commandSeq: Seq[String] = executor :+ s"${workDirName}/${scriptName}" :+ s"${workDirName}/data.json" :+ s"${workDirName}/args.json"
    val result = commandSeq.!!
    deleteWorkDirFun()
    result
  }
}