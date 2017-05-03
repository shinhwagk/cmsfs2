package org.cmsfs.role.service

import akka.actor.{Actor, ActorLogging, Props}
import org.cmsfs.config.db.QueryConfig
import org.cmsfs.config.db.table.ConfTaskService
import org.cmsfs.role.service.Servicer.{ServiceConfig, ServicerConfig}

import scala.util.{Failure, Success}

class ServiceWorker extends Actor with ActorLogging {

  log.info("ServiceWorker start.")

  import context.dispatcher

  override def receive: Receive = {
    case ServiceMessages.WorkerJob(confService: ConfTaskService, env: Map[String, String], processResult: String) => {

      println(s"service worker processResult: ${processResult}")

      val serviceId = confService.id
      val serviceArgs = confService.args
      QueryConfig.getCoreServiceById(serviceId) onComplete {
        case Success(coreService) =>
          val serviceConfig = ServiceConfig(coreService.files, serviceArgs)
          val servicerConfig = ServicerConfig(processResult, serviceConfig, env)
          Servicer.executeService(servicerConfig)

        case Failure(ex) => log.error("coreService query error: " + ex.getMessage)
      }
    }
  }
}

object ServiceWorker {
  val props = Props[ServiceWorker]
}