package org.cmsfs.servie.elasticsearch

object ElasticSearchMessage {

  case class MetaData(_index: String, _type: String, _metric: String, _utcDate: String, dslName: String)

  case class WorkerJob(documents: String, metaData: MetaData)

}
