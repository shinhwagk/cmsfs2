package org.cmsfs.role.collect.jdbc

import java.sql.{DriverManager, ResultSet}

import play.api.libs.json.{JsString, JsValue, Json}

import scala.collection.immutable.Map
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}

class CollectingMysql(jdbcUrl: String, username: String, password: String, sqlText: String, parameters: Seq[String])
                     (implicit ec: ExecutionContext) {

  Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

  def mode(mode: String): Future[String] = {
    QueryModeEnum.withName(mode.toUpperCase) match {
      case QueryModeEnum.ARRAY => query[List[String]](queryToAarry)
      case QueryModeEnum.MAP => query[Map[String, String]](queryToMap)
    }
  }

  def query[T](f: (ResultSet) => JsValue) = Future {
    val conn = DriverManager.getConnection(jdbcUrl, username, password)
    val stmt = conn.prepareStatement(sqlText)
    (1 to parameters.length).foreach(num => stmt.setObject(num, 5))
    val rs = stmt.executeQuery()

    val rows = f(rs)

    stmt.close()
    rs.close()
    conn.close()

    rows.toString()
  }

  def queryToMap(rs: ResultSet): JsValue = {
    val meta = rs.getMetaData
    val rows = new ArrayBuffer[Map[String, JsValue]]()
    import scala.collection.mutable.Map
    while (rs.next()) {
      val row: Map[String, JsValue] = Map.empty
      for (i <- 1 to meta.getColumnCount) {
        row += (meta.getColumnName(i) -> JsString(rs.getString(i)))
      }
      rows += row.toMap
    }
    Json.toJson(rows.toList)
  }

  def queryToAarry(rs: ResultSet): JsValue = {
    val meta = rs.getMetaData
    val rows: ArrayBuffer[List[String]] = new ArrayBuffer[List[String]]()
    rows += (1 to meta.getColumnCount).toList.map(meta.getColumnName)
    while (rs.next()) {
      rows += (1 to meta.getColumnCount).toList.map(i => (if (rs.getString(i) == null) "" else rs.getString(i)))
    }
    Json.toJson(rows.toList)
  }
}