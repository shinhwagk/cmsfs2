package org.cmsfs.role.collect.jdbc

object QueryModeEnum extends Enumeration {
  type QueryMode = Value
  val ARRAY = Value("ARRAY")
  val MAP = Value("MAP")
}
