package se.gigurra.util

import java.io.File
import java.io.FileNotFoundException

object Resource {

  def file2String(path: String, enc: String = "UTF-8"): String = {
    new File("src/main/resources/" + path) match {
      case f if f.exists() => scala.io.Source.fromFile(f, enc).mkString
      case _ =>
        new File(path) match {
          case f if f.exists() => scala.io.Source.fromFile(f, enc).mkString
          case _ => Option(ClassLoader.getSystemClassLoader.getResourceAsStream(path)) match {
            case Some(str) if str.available() > 0 => scala.io.Source.fromInputStream(str, enc).mkString
            case _ => Option(getClass.getClassLoader.getResourceAsStream(path)) match {
              case Some(str) if str.available() > 0 => scala.io.Source.fromInputStream(str, enc).mkString
              case _                                => throw new FileNotFoundException(path)
            }
          }
        }
    }

  }

}
