package com.dataframe.part5.JustinPihonyFileSearchPRoject

/**
  * Created by kalit_000 on 5/25/19.
  */
class FilterChecker(filter:String) {

  def matches(content:String) = content.contains(filter)

  def findMatchedFiles(iOObjects : List[IOObject])={
    for(iOObject <- iOObjects
        if(iOObject.isInstanceOf[FileObject2]) // checking ioobject actually a file
        if(matches(iOObject.name))
       ) yield iOObject
  }
}


object FilterChecker {
  def apply(filter: String): FilterChecker = new FilterChecker(filter)
}