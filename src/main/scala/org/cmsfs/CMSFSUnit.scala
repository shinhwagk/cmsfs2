package org.cmsfs

case class CMSFSUnit(mode: String, path: String, args: Option[String], next: Seq[CMSFSUnit])
case class CMSFSCollect()
case class CMSFSFormat()
