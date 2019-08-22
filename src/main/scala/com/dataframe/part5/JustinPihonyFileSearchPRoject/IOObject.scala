package com.dataframe.part5.JustinPihonyFileSearchPRoject

/**
  * Created by kalit_000 on 5/25/19.
  */

import java.io.File
trait IOObject {
  val file: File
  val name: String = file.getName
}

case class FileObject2(file:File) extends IOObject
case class DirectoryObject(file:File) extends IOObject {
  def children() =
    try {
      file.listFiles().toList map (file => FileConverter convertToIOOject (file))
    }
  catch {
    case _ : NullPointerException => List()
  }
}
