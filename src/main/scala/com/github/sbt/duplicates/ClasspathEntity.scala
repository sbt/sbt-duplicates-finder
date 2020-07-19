package com.github.sbt.duplicates

import java.io.InputStream
import java.nio.file.Files
import java.security.{DigestInputStream, MessageDigest}

import sbt._
import sbt.io.Using

/**
  * Lazy & comparable handle to a class or resource within a zip file or directory.
  * Caches the checksum.
  * Falls back to reading the full file contents if absolutely necessary.
  */
sealed trait ClasspathEntity {

  /**
    * Name of the zip file or directory.
    */
  def source: File

  /**
    * Path within the zip file or directory.
    *
    * @example `scala/None$.class`
    * @example `META-INF/MANIFEST.MF`
    */
  def name: String

  /**
    * The content of the class file or resource.
    */
  protected def withInputStream[T](f: InputStream => T): T

  lazy val sha256: Seq[Byte] =
    withInputStream { in =>
      val buf = Array.ofDim[Byte](8192)
      val dis = new DigestInputStream(in, MessageDigest.getInstance("SHA-256"))
      while (dis.read(buf) != -1) {}
      dis.getMessageDigest.digest().toSeq
    }

  override def equals(obj: Any): Boolean =
    obj match {
      case null => false
      case other: ClasspathEntity =>
        name == other.name && sha256 == other.sha256
      case _ => false
    }

  override def hashCode: Int =
    // just for completeness
    sha256(0) & 0xff |
      sha256(1) & 0xff << 8 |
      sha256(2) & 0xff << 16 |
      sha256(3) & 0xff << 24

  override def toString: String = s"$source/$name"
}

final class ZipEntity(
    val source: File,
    val name: String
) extends ClasspathEntity {

  override protected def withInputStream[T](f: InputStream => T): T =
    Using.zipFile(source) { zipFile =>
      Option(zipFile.getEntry(name))
        .map(entry => Using.bufferedInputStream(zipFile.getInputStream(entry))(f))
        .getOrElse(throw new IllegalStateException(s"Could not get $name from $source"))
    }
}

final class FileEntity(
    val source: File,
    val name: String
) extends ClasspathEntity {

  override protected def withInputStream[T](f: InputStream => T): T =
    Using.bufferedInputStream(Files.newInputStream((source / name).toPath))(f)
}
