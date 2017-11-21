package com.typesafe.sbt.duplicates

import sbt._
import sbt.io.Path

object Compat {

  def relativeTo(base: File) = Path.relativeTo(base)

  val Using = sbt.io.Using
  type IvySbt = sbt.internal.librarymanagement.IvySbt
}