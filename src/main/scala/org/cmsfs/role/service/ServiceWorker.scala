package org.cmsfs.role.service

import akka.actor.{Actor, ActorLogging, Props}
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.config.db.table.ConfTaskService
import org.cmsfs.role.service.Servicer.{ServiceConfig, ServicerConfig}

import scala.util.{Failure, Success, Try}

class ServiceWorker extends Actor with ActorLogging {

  log.info("ServiceWorker start.")

  import context.dispatcher

  override def receive: Receive = {
    case ServiceMessages.WorkerJob(confService: ConfTaskService, env: Map[String, String], processResult: String) => {

      val serviceId = confService.id
      val serviceArgs = confService.args
      QueryConfig.getCoreServiceById(serviceId) onComplete {
        case Success(coreService) =>
          Try {
            val serviceConfig = ServiceConfig(coreService.files, serviceArgs)
            val servicerConfig = ServicerConfig(processResult, serviceConfig, env)
            Servicer.executeService(servicerConfig)
          } match {
            case Success(v) =>
              println(s"service: ${env.get("collect-name")}, ${env.get("conn-name")}")
            case Failure(e) =>
              println("Info from the exception: " + e.getMessage)
              log.error(s"service: ${env.get("collect-name")}, ${env.get("conn-name")}")
          }
        case Failure(ex) => log.error("coreService query error: " + ex.getMessage)
      }
    }
  }
}

object ServiceWorker {
  val props = Props[ServiceWorker]
}