package com.github.sbt.duplicates

import sbt.File

case class Checksum(source: File, checksum: String)