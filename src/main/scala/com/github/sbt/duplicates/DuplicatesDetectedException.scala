package com.github.sbt.duplicates

class DuplicatesDetectedException(message: String) extends sbt.FeedbackProvidedException {
  override def toString: String = message
}
