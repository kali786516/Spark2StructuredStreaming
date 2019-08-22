package com.dataframe.part22.strcuturedStreamingExamples

/**
  * Created by kalit_000 on 5/17/19.
  */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.types._

object FileStreamAppendMode {
  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark = SparkSession.builder
      .master("local[3]")
      .appName("CSVFileStream")
      //.config("spark.driver.memory","2g")
      //.config("spark.cassandra.connection.host","localhost")
      //.enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    val schemaUntyped = new StructType()
      .add("lsoa_code", "string")
      .add("borough", "string")
      .add("major_category", "string")
      .add("minor_category", "string")
      .add("value", "string")
      .add("year", "string")
      .add("month", "string")

    val fileStreamDF = spark.readStream.option("header","true").schema(schemaUntyped).csv("StreamDataSource/droplocation")

    println(fileStreamDF.isStreaming)

    println(fileStreamDF.printSchema())

    val trimmedDF = fileStreamDF.select($"borough",$"year",$"month",$"value").withColumnRenamed("value","convictions")

    // append is real time stream .....
    // complete is like update state by key
    val query = trimmedDF.writeStream.outputMode("append")
                         .format("console")
                         .option("truncate","false")
                         .option("numRows",30)
                         .start()
                         .awaitTermination()

  }

}
