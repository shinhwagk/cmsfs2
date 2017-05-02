package org.cmsfs.common

import java.io.File
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.ThreadLocalRandom

import com.typesafe.config.ConfigFactory
import org.apache.commons.io.FileUtils
import org.cmsfs.common.ScriptExecutorMode.ScriptExecuteMode
import play.api.libs.json.Json

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object ScriptExecute {

  private val formatUrl: String = ConfigFactory.load().getString("cmsfs.url")

  def getUrlByPath(path: Seq[String]): String = {
    formatUrl +: path mkString "/"
  }

  def getUrlContentByPath(path: Seq[String]): String = {
    Source.fromURL(getUrlByPath(path), "UTF-8").mkString.trim
  }

  private def createWorkDirAndDeleteAfterExecute(): (String, () => Unit) = {
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
    dirPath + "/" + scriptName
  }

  private def downScript(file: Seq[String], dirPath: String): String = {
    val url = getUrlByPath(file)
    val scriptName = file.last
    FileUtils.copyURLToFile(new URL(url), new File(dirPath + "/" + scriptName))
    dirPath + "/" + scriptName
  }

  private def writeFile(fileName: String, content: String): Unit =
    FileUtils.writeStringToFile(new File(fileName), content, Charset.forName("UTF-8"), false)

  private def executorChoice(url: String, executeMode: ScriptExecuteMode): Seq[String] = {
    val name = url.split("\\.").last
    if (ScriptExecutorMode.DOWN == executeMode) {
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

  def executeScript(files: Seq[Seq[String]], env: String, data: Option[String], args: Option[String], executeMode: ScriptExecuteMode, resultToArrayByLine: Boolean): String = {
    val url = getUrlByPath(files(0))
    val rs = executeMode match {
      case ScriptExecutorMode.ONLINE =>
        executeScriptForOnline(files, data, args)
      case ScriptExecutorMode.DOWN =>
        executeScriptForDown(files, data, args)
    }
    if (resultToArrayByLine) rs.split("\n").map(_.trim).mkString("[\"", "\",\"", "\"]") else rs.trim
  }

  private def executeScriptForOnline(files: Seq[Seq[String]], data: Option[String], args: Option[String]): String = {
    val url = getUrlByPath(files(0))
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
      executeScriptForDown(files, data, args)
    }
  }

  private def executeScriptForDown(files: Seq[Seq[String]], dataOpt: Option[String], argsOpt: Option[String]): String = {
    import sys.process._
    var executor: Seq[String] = executorChoice(files(0).last, ScriptExecutorMode.DOWN)
    val (workDirName, deleteWorkDirFun) = createWorkDirAndDeleteAfterExecute()

    files.foreach(downScript(_, workDirName))

    executor = executor :+ files(0).last

    if (dataOpt.isDefined) {
      writeData(dataOpt.get, workDirName)
      executor = executor :+ (s"${workDirName}/data.json")
    }

    if (argsOpt.isDefined) {
      writeArgs(argsOpt.get, workDirName)
      executor = executor :+ (s"${workDirName}/args.json")
    }

    val result = executor.!!.trim
    deleteWorkDirFun()
    result
  }
}
