package org.cmsfs.servie.bootstrap

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.routing.RoundRobinPool
import org.cmsfs.Common
import org.cmsfs.servie.CmsfsClusterInfo
import org.cmsfs.servie.bootstrap.BootstrapActor.MessageScheduler
import org.cmsfs.servie.bootstrap.SchedulerActor.{RegisterCollectScriptLocal, RegisterCollectScriptRemote}

import scala.concurrent.duration._

class BootstrapActor extends Actor with ActorLogging {

  var CollectScriptLocalsMembers = IndexedSeq.empty[Member]

  var CollectScriptRemotesMembers = IndexedSeq.empty[Member]

  var FormatScriptsMembers = IndexedSeq.empty[Member]

  val cluster = Cluster(context.system)

  val schedulerActor: ActorRef = context.actorOf(RoundRobinPool(10).props(SchedulerActor.props), "scheduler")

  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp], classOf[MemberJoined], classOf[MemberEvent], classOf[UnreachableMember], classOf[MemberRemoved])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberJoined(member) =>
      log.info("Member is Join: {}. role: {}", member.address, member.roles)
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}", member.address, member.roles)
      registerMember(member)
    case state: CurrentClusterState =>
      log.info("Current members: {}", state.members.mkString(", "))
      state.members.filter(_.status == MemberStatus.Up) foreach registerMember
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
      unRegisterMember(member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case MessageScheduler =>
      println("local", CollectScriptLocalsMembers)
      println("remote", CollectScriptRemotesMembers)
      println("format", FormatScriptsMembers)

      schedulerAction()
    case _: MemberEvent => // ignore
  }

  def schedulerAction() = {
    schedulerActor ! RegisterCollectScriptLocal("disk_space", CollectScriptLocalsMembers)
    schedulerActor ! RegisterCollectScriptRemote(CollectScriptRemotesMembers)
  }

  import CmsfsClusterInfo._

  def registerMember(member: Member): Unit = {
    CollectScriptLocalsMembers = Common.registerMember(member, Role_Collect_Script_Local, CollectScriptLocalsMembers)
    CollectScriptRemotesMembers = Common.registerMember(member, Role_Collect_Script_Remote, CollectScriptRemotesMembers)
    FormatScriptsMembers = Common.registerMember(member, Role_Format_Script, FormatScriptsMembers)
  }

  def unRegisterMember(member: Member): Unit = {
    CollectScriptLocalsMembers = Common.unRegisterMember(member, Role_Collect_Script_Local, CollectScriptLocalsMembers)
    CollectScriptRemotesMembers = Common.unRegisterMember(member, Role_Collect_Script_Remote, CollectScriptRemotesMembers)
    FormatScriptsMembers = Common.unRegisterMember(member, Role_Format_Script, FormatScriptsMembers)
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