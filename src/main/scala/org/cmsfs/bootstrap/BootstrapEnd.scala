package org.cmsfs.bootstrap

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberRemoved, MemberUp, UnreachableMember}
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future

class BootstrapEnd extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  val workerRouter = context.actorOf(FromConfig.props(), name = "workerRouter")
//  println(workerRouter.path.toString)
 val workerRouter2 = context.actorSelection("akka.tcp://ClusterSystem@127.0.0.1:2562/user/collect-script/collect-script")

  implicit val x = cluster.system.dispatcher

  Future {
    while (true) {
      Thread.sleep(5000);
      println("start")
      workerRouter2 ! "abc"
    }
  }


  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
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

object BootstrapEnd {
  def main(args: Array[String]): Unit = {
    val port = args(0)
    val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
      .withFallback(ConfigFactory.load("bootstrap"))

    val system = ActorSystem("ClusterSystem", config)
    val bootstrap = system.actorOf(Props[BootstrapEnd], name = "bootstrap")
  }
}