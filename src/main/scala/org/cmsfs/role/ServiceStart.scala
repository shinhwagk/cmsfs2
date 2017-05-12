package org.cmsfs.role

import akka.actor.{Actor, Props}
import org.cmsfs.Common

import scala.reflect.ClassTag

abstract class ServiceStart[T <: Actor]()(implicit c: ClassTag[T]) {
  def main(args: Array[String]): Unit = {
    val system = Common.genActorSystem(args)
    system.actorOf(Props[T], name = args(1))
  }
}
