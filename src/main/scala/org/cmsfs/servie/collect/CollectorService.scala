package org.cmsfs.servie.collect

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member, MemberStatus}
import org.cmsfs.ClusterInfo._
import org.cmsfs.Common
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.servie.collect.Collector.{CollectConfig, CollectorEnv}

import scala.collection.mutable
import scala.util.{Failure, Success}

class CollectorService extends Actor with ActorLogging {

  log.info("CollectorService start.")

  val memberNames: Seq[String] =
    Service_Collect_Ssh ::
      Service_Collect_Jdbc ::
      Service_Collect_Local ::
      Nil

  val serviceMembers: mutable.Map[String, IndexedSeq[Member]] = Common.initNeedServices(memberNames)

  val cluster = Cluster(context.system)

  import context.dispatcher

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}", member.address, member.roles)
      Common.registerMember(member, serviceMembers)
    case state: CurrentClusterState =>
      log.info("Current members: {}", state.members.mkString(", "))
      state.members.filter(_.status == MemberStatus.Up).foreach(member => Common.registerMember(member, serviceMembers))
    case UnreachableMember(member) =>
      println("Member detected as unreachable: {}", member, member.status)
      Common.unRegisterMember(member, serviceMembers)
    case CollectorServiceMessage.WorkerJob(task, utcDate) =>
      QueryConfig.getCoreCollectById(task.collect.id).foreach { collect =>
        val conf = CollectConfig(collect.files, task.collect.args)
        val env = CollectorEnv(utcDate, collect.name)
        collect.mode match {
          case "collect-ssh" =>
            val workers: IndexedSeq[Member] = serviceMembers.get(Service_Collect_Ssh).get
            if (workers.length >= 1) {
              context.actorSelection(RootActorPath(workers(0).address) / "user" / Actor_Collect_Ssh) ! CollectorWorkerMessage.WorkerJob(task, conf, env)
            } else {
              log.error(s"collect service ${Service_Collect_Ssh} less.")
            }
          case "collect-jdbc" =>
            val workers: IndexedSeq[Member] = serviceMembers.get(Service_Collect_Jdbc).get
            if (workers.length >= 1) {
              context.actorSelection(RootActorPath(workers(0).address) / "user" / Actor_Collect_Jdbc) ! CollectorWorkerMessage.WorkerJob(task, conf, env)
            } else {
              log.error(s"collect service ${Service_Collect_Jdbc} less.")
            }
          case _ => println("unkonws")
        }
      }
    case _: MemberEvent => // ignore
  }
}

object CollectorService {
  val props = Props[CollectorService]
}