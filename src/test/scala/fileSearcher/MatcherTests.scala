package fileSearcher

import com.dataframe.part5.JustinPihonyFileSearchPRoject.Matcher
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

/**
  * Created by kalit_000 on 5/25/19.
  */

import com.dataframe.part5._
import java.io.File

@RunWith(classOf[JUnitRunner])
class MatcherTests extends FlatSpec {
  "Matcher that is passed a file matching the filter" should
  "return a list with that file name" in {
    val matcher = new Matcher("fake","fakepath")

    val results = matcher.execute()

    assert(results == List("fakePath"))
  }

  "Matcher using a directory containing one file matching the filter" should
  "return a list with that file name" in {
    val matcher = new Matcher("txt",new File("testfiles/").getCanonicalPath)

    val results = matcher.execute()

    assert(results == List("readme.txt"))
  }

  "Matcher that is not passed a root file location" should
  "use the current location" in {
    val matcher = new Matcher("filter")
    assert(matcher.rootLocation == new File(".").getCanonicalPath())
  }


}
