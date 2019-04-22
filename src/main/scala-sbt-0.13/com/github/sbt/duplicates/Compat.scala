package com.github.sbt.duplicates

import sbt._

object Compat {

  def relativeTo(base: File) = sbt.relativeTo(base)

  val Using = sbt.Using
  type IvySbt = sbt.IvySbt
}
