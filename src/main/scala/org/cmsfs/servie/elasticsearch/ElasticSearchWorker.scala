package org.cmsfs.servie.elasticsearch

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.ActorMaterializer
import org.cmsfs.common.JsonFormat
import play.api.libs.ws.StandaloneWSClient
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.Future

class ElasticSearchWorker extends Actor with ActorLogging {

  implicit val system = context.system
  implicit val materializer = ActorMaterializer()

  import context.dispatcher

  val wsClient: StandaloneAhcWSClient = StandaloneAhcWSClient()

  var processCapacityCalculate = 0L

  override def receive: Receive = {
    case job: ElasticSearchMessage.WorkerJob =>
      processCapacityCalculate += 1
      val document = JsonFormat.formatResultAddFiled(job.documents, job.metaData._utcDate, job.metaData._metric)
      call(wsClient, document, job.metaData._index, job.metaData._type)
  }

  def call(wsClient: StandaloneWSClient, rs: String, _index: String, _type: String): Unit = {
    wsClient.url(s"http://10.65.103.63:9200/${_index}/${_type}").post(rs).foreach { response =>
      val statusText: String = response.statusText
      processCapacityCalculate -= 1
      if (processCapacityCalculate >= 10) {
        println(s"Got a response $statusText , ${processCapacityCalculate}")
      }
    }
  }
}

object ElasticSearchWorker {
  val props = Props[ElasticSearchWorker]
}