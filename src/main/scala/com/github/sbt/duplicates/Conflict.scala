package com.github.sbt.duplicates

import sbt.File

case class Conflict(name: String, conflicts: List[File], conflictState: ConflictState)
