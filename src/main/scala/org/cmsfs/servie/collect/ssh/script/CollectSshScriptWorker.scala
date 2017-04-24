package org.cmsfs.servie.collect.ssh.script

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.ClusterInfo._
import org.cmsfs.common.{ScriptExecute, ScriptExecutorMode}
import org.cmsfs.servie.collect.ssh.script.CollectSshScriptMessages.WorkerJob
import org.cmsfs.servie.format.FormatScriptMessages
import org.cmsfs.servie.format.FormatScriptMessages.Format

import scala.util.Random

class CollectSshScriptWorker(formatMembers: IndexedSeq[Member]) extends Actor with ActorLogging {
  override def receive: Receive = {
    case job: WorkerJob =>
      val path = job.collect.path
      val args = job.collect.args
      val rs: String = ScriptExecute.executeScript(path, None, args, ScriptExecutorMode.ONLINE, true)
      val random_index = new Random().nextInt(formatMembers.length)
      val member = formatMembers(random_index)
      val format = Format("a", Some("a"), "")
      context.actorSelection(RootActorPath(member.address) / "user" / Service_Format_Script) ! FormatScriptMessages.WorkerJob(rs, format)
  }
}

object CollectSshScriptWorker {
  def props(formatMembers: IndexedSeq[Member]) = Props(new CollectSshScriptWorker(formatMembers))
}