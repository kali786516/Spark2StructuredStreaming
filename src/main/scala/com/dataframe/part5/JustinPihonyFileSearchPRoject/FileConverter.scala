package com.dataframe.part5.JustinPihonyFileSearchPRoject

/**
  * Created by kalit_000 on 5/25/19.
  */

import java.io.File

object FileConverter {
  def convertToIOOject(file:File) =
      if(file.isDirectory()) DirectoryObject(file)
      else FileObject2(file)
}
