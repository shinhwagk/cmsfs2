package org.cmsfs.servie.bootstrap

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.routing.RoundRobinPool
import org.cmsfs.Common
import org.cmsfs.servie.CmsfsClusterInfo
import org.cmsfs.servie.CmsfsClusterInfo._
import org.cmsfs.servie.bootstrap.BootstrapActor.MessageScheduler
import org.cmsfs.servie.bootstrap.SchedulerActor.{RegisterCollectJdbc, RegisterCollectScriptLocal, RegisterCollectScriptRemote}

import scala.collection.mutable
import scala.concurrent.duration._

class BootstrapActor extends Actor with ActorLogging {

  val memberNames: Seq[String] =
    Service_Collect_Script_Remote ::
      Service_Collect_Script_Local ::
      Service_Collect_Jdbc ::
      Nil

  val serviceMembers: mutable.Map[String, IndexedSeq[Member]] = Common.initNeedServices(memberNames)

  val cluster = Cluster(context.system)

  val schedulerActor: ActorRef = context.actorOf(RoundRobinPool(10).props(SchedulerActor.props), "scheduler")

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp],
      classOf[MemberJoined], classOf[MemberEvent], classOf[UnreachableMember], classOf[MemberRemoved],
      classOf[MemberLeft],classOf[MemberExited])

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
      cluster.leave(member.address)
    case MemberLeft(member) =>
      println("Member is Leaving: {} ", member.address, member.status)
    case MemberExited(member) =>
      println("Member is Exited: {} ", member.address, member.status)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case MessageScheduler =>
      schedulerAction()
    case _: MemberEvent => // ignore
  }

  def schedulerAction() = {
    serviceMembers.foreach { member =>
      member._1 match {
        case Service_Collect_Script_Local =>
          schedulerActor ! RegisterCollectScriptLocal("disk_space", member._2)
        case _ =>
      }
    }

    //    schedulerActor ! RegisterCollectScriptRemote(serviceMembers.get(Service_Collect_Script_Remote).get)
    //    schedulerActor ! RegisterCollectJdbc(serviceMembers.get(Service_Collect_Jdbc).get)
  }
}

object BootstrapActor {

  import CmsfsClusterInfo._

  def main(args: Array[String]): Unit = {
    val port = args(0)
    val system = Common.genActorSystem(Role_Bootstrap, port)
    val bootstrap = system.actorOf(Props[BootstrapActor], name = Service_Bootstrap)

    import system.dispatcher

    system.scheduler.schedule(2.seconds, 2.seconds, bootstrap, MessageScheduler)
  }

  case object MessageScheduler

}