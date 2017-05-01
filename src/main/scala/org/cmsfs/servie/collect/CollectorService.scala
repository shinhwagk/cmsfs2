package org.cmsfs.servie.collect

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member, MemberStatus}
import org.cmsfs.Common
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.servie.collect.Collector.{CollectConfig, CollectorConfig, CollectorEnv}
import org.cmsfs.servie.collect.CollectorServiceMessage.WorkerJob

import scala.collection.mutable

class CollectorService extends Actor with ActorLogging {
  val memberNames: Seq[String] =
  //    Service_Collect_Ssh_Script ::
  //    Service_Collect_Local_Script ::
  //    Service_Collect_Jdbc ::
    Nil

  val serviceMembers: mutable.Map[String, IndexedSeq[Member]] = Common.initNeedServices(memberNames)

  val cluster = Cluster(context.system)

  import context.dispatcher

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp],
      classOf[MemberJoined], classOf[MemberEvent], classOf[UnreachableMember], classOf[MemberRemoved],
      classOf[MemberLeft], classOf[MemberExited])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberJoined(member) =>
      log.info("Member is Join: {}. role: {}", member.address, member.roles)
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}", member.address, member.roles)
      Common.registerMember(member, serviceMembers)
    case state: CurrentClusterState =>
      log.info("Current members: {}", state.members.mkString(", "))
      state.members.filter(_.status == MemberStatus.Up).foreach(member => Common.registerMember(member, serviceMembers))
    case UnreachableMember(member) =>
      println("Member detected as unreachable: {}", member, member.status)
      Common.unRegisterMember(member, serviceMembers)
    case MemberLeft(member) =>
      println("Member is Leaving: {} ", member.address, member.status)
    case MemberExited(member) =>
      println("Member is Exited: {} ", member.address, member.status)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case WorkerJob(task, utcDate) =>
      QueryConfig.getCoreCollectById(task.collect.collectId).foreach { collect =>
        val conf = CollectConfig(collect.path, collect.files, task.collect.args)
        val env = CollectorEnv(utcDate, collect.name)
        collect.mode match {
          case "collect-ssh-script" =>
            val workers: IndexedSeq[Member] = serviceMembers.get("ssh").get
            context.actorSelection(RootActorPath(workers(0).address) / "user" / "ssh" / "worker") ! CollectorWorkerMessage.WorkerJob(task, conf, env)
          case "collect-jdbc" =>
            val workers: IndexedSeq[Member] = serviceMembers.get("jdbc").get
            context.actorSelection(RootActorPath(workers(0).address) / "user" / "ssh" / "worker") ! CollectorWorkerMessage.WorkerJob(task, conf, env)
          case _ => println("unkonws")
        }
      }
    case _: MemberEvent => // ignore
  }
}

object CollectorService {
  val props = Props[CollectorService]
}