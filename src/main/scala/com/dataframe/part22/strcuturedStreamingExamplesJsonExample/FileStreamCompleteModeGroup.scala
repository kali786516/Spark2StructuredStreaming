package com.dataframe.part22.strcuturedStreamingExamplesJsonExample

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

object FileStreamCompleteModeGroup {
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

    val fileStreamDF = spark.readStream.option("header","true")
      .option("maxFilesPerTrigger",1)
      .schema(schemaUntyped).csv("StreamDataSource/droplocation")

    println(fileStreamDF.isStreaming)

    println(fileStreamDF.printSchema())

    val busDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    spark.udf.register("addTimeStamp",() => busDataFormat.format(Calendar.getInstance().getTime()))

    val trimmedDF = fileStreamDF.select($"value",$"major_category",$"minor_category",$"year",$"month",$"borough").withColumnRenamed("value","convictions")

    trimmedDF.createOrReplaceTempView("test")

    val df2=spark.sql("with cte as (select convictions,addTimeStamp() as timestamp_value from test) select sum(convictions) as convictions,timestamp_value from cte group by timestamp_value")

    val query = df2.writeStream.outputMode("complete")
      .format("console")
      .option("truncate","false")
      .trigger(Trigger.ProcessingTime("5 seconds"))
      //.option("numRows",30)
      .start()
      .awaitTermination()


  }

}
