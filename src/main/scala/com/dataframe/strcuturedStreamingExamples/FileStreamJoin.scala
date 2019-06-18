package com.dataframe.strcuturedStreamingExamples

/**
  * Created by kalit_000 on 5/17/19.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.types._
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar

import org.json4s.DateFormat
object FileStreamJoin {
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

    val personalDetailsSchema = new StructType()
      .add("customer_id", "string")
      .add("gender", "string")
      .add("age", "string")

    val transactionDetailsSchema = new StructType()
      .add("customer_id", "string")
      .add("transaction_amount", "string")
      .add("transaction_rating", "string")

    val customerDF = spark.read.format("csv").option("header","true").schema(personalDetailsSchema)
                          .load("/Users/kalit_000/Downloads/Spark2StructuredStreaming/StreamDataSource/customerDatasets/static_datasets/join_static_personal_details.csv")

    val fileStreamDF = spark.readStream.option("header","true").option("maxFilesPerTrigger",1)
                                       .schema(transactionDetailsSchema)
                                       .csv("StreamDataSource/customerDatasets/streaming_datasets/join_streaming_transaction_details/")

    val joinedDF = customerDF.join(fileStreamDF,"customer_id")

    //joinedDF.show()

    val query = joinedDF.writeStream.outputMode("append").format("console").start().awaitTermination()





  }

}
