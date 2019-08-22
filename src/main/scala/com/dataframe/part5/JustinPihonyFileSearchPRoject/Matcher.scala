package com.dataframe.part5.JustinPihonyFileSearchPRoject

/**
  * Created by kalit_000 on 5/25/19.
  */
import java.io.File

import com.dataframe.part5._

class Matcher (filter:String,val rootLocation:String = new File(".").getCanonicalPath()) {
  val rootIOObject = FileConverter.convertToIOOject(new File(rootLocation))

  def execute() = {
    val matchFiles = rootIOObject match {
      case file: FileObject2 if FilterChecker(filter) matches file.name => List(file)
      case directory: DirectoryObject =>
        FilterChecker(filter) findMatchedFiles directory.children()
      case _ => List()
    }

    matchFiles map(ioobject => ioobject.name)
  }
}
