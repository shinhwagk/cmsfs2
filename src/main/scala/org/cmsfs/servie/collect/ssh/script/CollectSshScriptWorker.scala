package org.cmsfs.servie.collect.ssh.script

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.ClusterInfo._
import org.cmsfs.servie.collect.ssh.script.CollectSshScriptMessages.WorkerJob
import org.cmsfs.servie.format.FormatScriptMessages
import org.cmsfs.servie.format.FormatScriptMessages.Format

import scala.collection.mutable
import scala.util.Random

class CollectSshScriptWorker(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) extends CollectSshScriptWorkerAction with Actor with ActorLogging {
  override def receive: Receive = {
    case job: WorkerJob =>
      val formatMembers = serviceMembers.get(Service_Format_Script).get
      if (formatMembers.length >= 1) {
        val path = job.collect.path
        val args = job.collect.args
        val ip = job.connect.ip
        val port = job.connect.port
        val user = job.connect.username
        val rs: Option[String] = executeScriptBySsh(ip, port, user, path)

        val random_index = new Random().nextInt(formatMembers.length)
        val member = formatMembers(random_index)
        val format = Format("a", Some("a"), "")
        context.actorSelection(RootActorPath(member.address) / "user" / Service_Format_Script) ! FormatScriptMessages.WorkerJob(rs.get, format)
      } else {
        println("format service member less.")
      }
  }
}

object CollectSshScriptWorker {
  def props(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) = Props(new CollectSshScriptWorker(serviceMembers))
}