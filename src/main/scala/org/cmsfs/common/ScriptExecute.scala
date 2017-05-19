package org.cmsfs.common

import java.io.File
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.ThreadLocalRandom

import com.typesafe.config.ConfigFactory
import org.apache.commons.io.FileUtils
import org.cmsfs.common.ScriptExecutorMode.ScriptExecuteMode
import play.api.libs.json.{JsValue, Json}

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

  def getUrlContentByPath(url:String): String = {
    Source.fromURL(url, "UTF-8").mkString.trim
  }

  private def createWorkDirAndDeleteAfterExecute(): (String, () => Unit) = {
    val dirName = s"workspace/${ThreadLocalRandom.current.nextLong(100000000).toString}"
    val dir = new File(dirName)
    FileUtils.forceMkdir(dir)

    (dirName, () => FileUtils.deleteDirectory(dir))
  }

  private def wirteFile(data: String, dirPath: String, fileName: String): Unit =
    writeFile(s"${dirPath}/${fileName}", data)

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

  private def executorChoice(mainFile: String, executeMode: ScriptExecuteMode): Seq[String] = {
    val name = mainFile.split("\\.").last
    if (ScriptExecutorMode.DOWN == executeMode) {
      name match {
        case "py" => Seq("python3")
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

  def executeScript(files: Seq[Seq[String]], env: Map[String, String], data: Option[String], args: Option[JsValue], executeMode: ScriptExecuteMode, resultToArrayByLine: Boolean): Option[String] = {
    val rs: Option[String] = executeMode match {
      case ScriptExecutorMode.ONLINE =>
        executeScriptForOnline(files, env, data, args)
      case ScriptExecutorMode.DOWN =>
        executeScriptForDown(files, env, data, args)
    }

    if (rs.isDefined) {
      rs.map { r =>
        if (resultToArrayByLine) r.split("\n").map(_.trim).mkString("[\"", "\",\"", "\"]") else r.trim
      }
    } else {
      None
    }
  }

  private def executeScriptForOnline(files: Seq[Seq[String]], env: Map[String, String], data: Option[String], args: Option[JsValue]): Option[String] = {
    val url = getUrlByPath(files(0))
    import sys.process._
    val executor: Seq[String] = executorChoice(url, ScriptExecutorMode.ONLINE)
    if (data.isDefined) {
      args match {
        case Some(args) =>
          val argsSeq: Seq[String] = Seq("")
          Some(Seq("curl", "-sk", url) #| (executor ++ argsSeq) !!)
        case None =>
          Some(Seq("curl", "-sk", url) #| executor !!)
      }
    } else {
      executeScriptForDown(files, env, data, args)
    }
  }

  private def executeScriptForDown(files: Seq[Seq[String]], env: Map[String, String], dataOpt: Option[String], argsOpt: Option[JsValue]): Option[String] = {
    import sys.process._

    val mainFile = files(0).last

    var executor: Seq[String] = executorChoice(mainFile, ScriptExecutorMode.DOWN)
    val (workDirName, deleteWorkDirFun) = createWorkDirAndDeleteAfterExecute()

    files.foreach(downScript(_, workDirName))

    executor = executor :+ mainFile

    if (dataOpt.isDefined) {
      wirteFile(dataOpt.get, workDirName, "data.json")
      executor = executor :+ "data.json"
    }

    if (argsOpt.isDefined) {
      wirteFile(argsOpt.get.toString(), workDirName, "args.json")
      executor = executor :+ "args.json"
    }

    val result: Option[String] = try {
      Some(Process(executor, new java.io.File(workDirName), env.toSeq: _*).!!)
    } catch {
      case ex: Exception =>
        println(ex.getMessage)
        None
    }
    deleteWorkDirFun()
    result
  }

}
