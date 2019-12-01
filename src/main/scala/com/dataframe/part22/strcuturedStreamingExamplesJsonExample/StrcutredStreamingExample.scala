package com.dataframe.part22.strcuturedStreamingExamplesJsonExample

/**
  * Created by kalit_000 on 5/16/19.
  */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

object StrcutredStreamingExample {
  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark = SparkSession.builder
      .master("local[*]")
      .appName("Fraud Detector")
      .config("spark.driver.memory","2g")
      .config("spark.cassandra.connection.host","localhost")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    val socketDS = spark.readStream.format("socket").option("host","localhost").option("port","9999").option("includeTimestamp",true).load

    socketDS.isStreaming

    val socketCount = socketDS.groupBy().count()

    val windowCount = socketDS.withWatermark("timestamp","10 seconds").groupBy(window($"timestamp","5 seconds","5 seconds"),$"value").count


    val query = socketCount.writeStream.format("console").outputMode("complete").start

    val query2 = socketCount.writeStream.format("memory").queryName("simpleCount").outputMode("complete").start

    spark.sql("select * from simpleCount").show()





  }

}
