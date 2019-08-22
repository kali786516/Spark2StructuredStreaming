package fileSearcher

/**
  * Created by kalit_000 on 5/25/19.
  */

import javax.tools.FileObject
import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest._
import com.dataframe.part5._
import java.io.File

import com.dataframe.part5.JustinPihonyFileSearchPRoject.{DirectoryObject, FileObject2, FilterChecker}

@RunWith(classOf[JUnitRunner])
class FilterCheckerTests extends FlatSpec {
  "FilterChecker passed a list where one file matched the filter" should
  "return a list with that file" in {
    val matchingFile =  FileObject2(new File("match"))
    val listOfFiles = List( FileObject2(new File("random")),FileObject2(new File("match")))
    val matchedFiles =  FilterChecker("match").findMatchedFiles(listOfFiles)
    assert(matchedFiles == List(FileObject2(new File("match"))))
  }

  "FilterChecker passed a list with a directory that matched the filter" should
  "should not return the directory" in {
    val listOfIOObjects = List( FileObject2(new File("random")),new DirectoryObject(new File("match")))
    val matchedFiles =  FilterChecker("match").findMatchedFiles(listOfIOObjects)
    assert(matchedFiles.length == 0 )
  }

}
