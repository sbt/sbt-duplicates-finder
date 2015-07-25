package com.typesafe.sbt.duplicates

sealed trait ConflictState
object ConflictState {
  case object ContentEqual extends ConflictState {
    override def toString = "same content"
  }
  case object ContentDiffer extends ConflictState {
    override def toString = "content differ"
  }
}
