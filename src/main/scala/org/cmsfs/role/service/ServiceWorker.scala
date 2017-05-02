package org.cmsfs.role.service

import akka.actor.{Actor, ActorLogging, Props}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

class ServiceWorker extends Actor with ActorLogging {

  val wsClient: StandaloneAhcWSClient = StandaloneAhcWSClient()

  override def receive: Receive = {
    case ServiceMessages.WorkerJob(method, url, body) => {

      val response = method.toUpperCase() match {
        case "POST" =>
          wsClient.url(url).post(body)
        case "GET" =>
          wsClient.url(url).get()
        case "DELETE" =>
          wsClient.url(url).delete()
        case "PUT" =>
          wsClient.url(url).put(body)
      }

      response.foreach { response =>
        val statusText: String = response.statusText
        println(s"Got a response $statusText")
      }

    }
  }
}

object ServiceWorker {
  val props = Props[ServiceWorker]
}