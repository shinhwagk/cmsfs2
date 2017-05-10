package org.cmsfs.role.collect

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}
import org.cmsfs.ClusterInfo._
import org.cmsfs.Common
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.config.db.table.ConfTaskSchema
import org.cmsfs.role.collect.Collector.CollectConfig

import scala.collection.mutable
import scala.util.Random

class CollectorService extends Actor with ActorLogging {

  log.info("CollectorService start.")

  /**
    * self service need services.
    *
    **/
  val memberNames: Seq[String] =
    Service_Collect_Ssh ::
      Service_Collect_Jdbc ::
      Service_Collect_Local ::
      Nil

  val serviceMembers: mutable.Map[String, IndexedSeq[Member]] = Common.initNeedServices(memberNames)

  val cluster = Cluster(context.system)

  import context.dispatcher

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp], classOf[UnreachableMember], classOf[ReachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}", member.address, member.roles)
      Common.registerMember(member, serviceMembers)
    //    case state: CurrentClusterState =>
    //      log.info("Current members: {}", state.members.mkString(", "))
    //      state.members.filter(_.status == MemberStatus.Up).foreach(member => Common.registerMember(member, serviceMembers))
    case ReachableMember(member) =>
      log.info("Member is Reachable: {}. role: {}", member.address, member.roles)
    case UnreachableMember(member) =>
      log.error("Member detected as unreachable: {}", member, member.status, member.roles)
      Common.unRegisterMember(member, serviceMembers)
    //      System.exit(1)
    case CollectorServiceMessage.WorkerJob(task, utcDate) =>
      QueryConfig.getCoreCollectById(task.collect.id).foreach { collect =>
        val conf: CollectConfig = CollectConfig(collect.file, task.collect.args)
        val env: Map[String, String] = Map("utc-date" -> utcDate, "collect-name" -> collect.name)
        val dFun = collectServiceDistributor(task, conf, env, utcDate) _
        collect.mode match {
          case s@Service_Collect_Ssh => dFun(s)
          case s@Service_Collect_Jdbc => dFun(s)
          case _ => println("unkonws")
        }
      }
    case _: MemberEvent => // ignore
  }

  def collectServiceDistributor(task: ConfTaskSchema, conf: CollectConfig, env: Map[String, String], utcDate: String)(serviceName: String) = {
    val workers: IndexedSeq[Member] = serviceMembers.get(serviceName).get
    if (workers.length >= 1) {
      val worker: Member = workers(new Random().nextInt(workers.length))
      context.actorSelection(RootActorPath(worker.address) / "user" / serviceName) !
        CollectorWorkerMessage.WorkerJob(task, conf, env)
    } else {
      log.error(s"collect service ${Service_Collect_Ssh} less.")
    }
  }
}

object CollectorService {
  val props = Props[CollectorService]
}