package org.cmsfs.common

import play.api.libs.json.{JsObject, JsString, JsValue, Json}

object JsonFormat {
//  def formatResultAddFiled(rs: Seq[JsValue], utcDate: String, metric: String): Seq[String] = {
//    rs.map(jsonObjectAddField(_, "@timestamp", utcDate))
//      .map(jsonObjectAddField(_, "@metric", metric))
//      .map(_.toString())
//  }

  def formatResultAddFiled(rs: String, utcDate: String, metric: String): String = {
    var json = Json.parse(rs)
    json = jsonObjectAddField(json, "@timestamp", utcDate)
    json = jsonObjectAddField(json, "@metric", metric)
    json.toString()
  }


  def jsonObjectAddField(json: JsValue, key: String, fieldVal: String): JsValue = {
    json.as[JsObject] + (key -> JsString(fieldVal))
  }
}
