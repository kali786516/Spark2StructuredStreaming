package com.dataframe.part20.DataFrameExamples

/**
  * Created by kalit_000 on 5/15/19.
  */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.types.{BooleanType, StructField, StructType}
import java.io._

object LocalIteratorExample {
  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._

    val pw = new PrintWriter(new File("Output/LocalFileTest"))

    val rdd = spark.sparkContext.parallelize(1 to 100).map(x => x)

    //used instead of collect

    for (line <- rdd.toLocalIterator) {

      pw.println(line)

    }

    pw.close()




  }
}
