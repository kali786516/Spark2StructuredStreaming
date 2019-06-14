package com.dataframe.extraDFExamples

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import java.io.{BufferedReader, InputStreamReader}
import java.util.zip.ZipInputStream
import org.apache.spark.SparkContext
import org.apache.spark.input.PortableDataStream
import org.apache.spark.rdd.RDD
import java.util.zip.ZipFile
/**
  * Created by kalit_000 on 6/14/19.
  */


object SparkReadZipFiles {

  implicit class ZipSparkContext(val sc: SparkContext) extends AnyVal {

    def readFile(path: String,
                 minPartitions: Int = sc.defaultMinPartitions): RDD[String] = {

      if (path.toLowerCase.contains("zip")) {

        sc.binaryFiles(path, minPartitions)
          .flatMap {
            case (zipFilePath, zipContent) ⇒
              val zipInputStream = new ZipInputStream(zipContent.open())
              Stream.continually(zipInputStream.getNextEntry)
                .takeWhile(_ != null)
                .map { x ⇒
                  val filename1 = x.getName
                  scala.io.Source.fromInputStream(zipInputStream, "UTF-8").getLines.mkString(s"~${filename1}\n")+s"~${filename1}"
                } #::: { zipInputStream.close; Stream.empty[String] }
          }
      } else {
        sc.textFile(path, minPartitions)
      }
    }
  }

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark = SparkSession.builder().appName("spark reading zip file").master("local[*]").getOrCreate()
    val sc = spark.sparkContext

    import spark.implicits._

    val rdd = sc.readFile("sparkZipDataSets/file_2.zip")

    val df = rdd.toDF("data")

    df.createOrReplaceTempView("table")

    spark.sql("select split(data,'~')[0] as data_values,split(data,'~')[1] as file_name from table").show(false)

    spark.stop()

  }

}
