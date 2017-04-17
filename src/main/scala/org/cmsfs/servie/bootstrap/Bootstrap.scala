package org.cmsfs.servie.bootstrap

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory
import org.cmsfs.Common
import org.cmsfs.servie.CmsfsClusterInfo

import scala.collection.mutable
import scala.concurrent.Future

class Bootstrap extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  //  val workerRouter = context.actorOf(FromConfig.props(), name = "workerRouter")
  //  println(workerRouter.path.toString)
  //  val workerRouter2 = context.actorSelection("akka.tcp://ClusterSystem@127.0.0.1:2562/user/collect-script/collect-script")

  //  implicit val x = cluster.system.dispatcher

  //  Future {
  //    while (true) {
  //      Thread.sleep(5000);
  //      println("start")
  //      workerRouter2 ! "abc"
  //    }
  //  }

  var backends = IndexedSeq.empty[String]

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp], classOf[MemberJoined])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberJoined(member) =>
      log.info("CMSFS Member is Join: {}. role: {}", member.address, member.roles)
    case MemberUp(member) =>
      log.info("Member is Up: {}. role: {}", member.address, member.roles)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore
  }
}

object Bootstrap {

  import CmsfsClusterInfo._

  def main(args: Array[String]): Unit = {
    val port = args(0)
    val system = Common.genActorSystem(Role_Bootstrap, port, Config_Bootstrap)
    system.actorOf(Props[Bootstrap], name = Service_Bootstrap)
  }
}