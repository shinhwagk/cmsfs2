package org.cmsfs.role.collect.ssh

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.Member
import org.cmsfs.ClusterInfo._
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.role.collect.{Collector, CollectorWorkerMessage}
import org.cmsfs.role.process.ProcessMessages

import scala.collection.mutable
import scala.util.{Failure, Random, Success}

class SshCollectWorker(serviceMembers: mutable.Map[String, IndexedSeq[Member]])
  extends Actor with ActorLogging {

  log.info("SshCollectWorker start.")

  import context.dispatcher

  override def receive: Receive = {
    case CollectorWorkerMessage.WorkerJob(task, conf, env) =>
      val connectId = task.collect.connect.get
      QueryConfig.getCoreConnectorSshById(connectId) onComplete {
        case Success(conn) => {
          try {
            val newEnv: Map[String, String] = env.+("conn-name" -> conn.name).+("conn-ip" -> conn.ip)
            val collectorConfig: Collector.CollectorConfig = Collector.SshCollectorConfig(conn, conf, env)
            val resultOpt: Option[String] = Collector.executeCollect(collectorConfig)()
            resultOpt foreach { result =>
              task.processes foreach { process =>
                val members: IndexedSeq[Member] = serviceMembers.get(Actor_Process).get
                if (members.length >= 1) {
                  val member: Member = members(new Random().nextInt(members.length))
                  context.actorSelection(RootActorPath(member.address) / "user" / Actor_Process) !
                    ProcessMessages.WorkerJob(process, result, newEnv, process.args)
                  //                  println("collect ssh ", env.get("collect-name"), env.get("conn-name"), env.get("utc-date"), new Date().toInstant.toString)
                } else {
                  log.error("process role less.")
                }
              }
            }
          } catch {
            case ex: Exception => log.error(ex.getMessage)
          }
        }
        case Failure(ex) => log.error(ex.getMessage)
      }
  }
}

object SshCollectWorker {
  def props(serviceMembers: mutable.Map[String, IndexedSeq[Member]]) = Props(new SshCollectWorker(serviceMembers))
}