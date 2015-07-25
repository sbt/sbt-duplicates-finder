package com.typesafe.sbt.duplicates

import sbt.File

case class Checksum(source: File, checksum: String)