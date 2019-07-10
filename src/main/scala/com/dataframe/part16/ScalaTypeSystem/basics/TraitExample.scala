package com.dataframe.part16.ScalaTypeSystem.basics
import java.io.File
import scala.io.Source

object TraitExample extends App{

  trait FileOps {
    self:File =>

    def isTextFile:Boolean = this.getName.endsWith("text")

    def readText:Iterator[String] = {
      if(this.isTextFile)
        Source.fromFile(this).getLines()
      else
        Iterator.empty
    }

    def printLines:Unit = {
      this.readText foreach println
    }

  }

  val fooTextFile = new File("/Users/kalit_000/Downloads/gitCode/Spark2StructuredStreaming/ingestionPocMetadata/ingestion_poc_metadata.txt") with FileOps

  println(fooTextFile.getName)
  println(fooTextFile.isTextFile)
  fooTextFile.printLines


}
