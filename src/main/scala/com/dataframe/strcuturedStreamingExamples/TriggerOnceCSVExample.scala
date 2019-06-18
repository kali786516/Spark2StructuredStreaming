package com.dataframe.strcuturedStreamingExamples

import java.text.SimpleDateFormat
import java.util.Calendar

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.callUDF
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.StructType

object TriggerOnceCSVExample {

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
      //.option("maxFilesPerTrigger",1)
      .schema(schemaUntyped).csv("StreamDataSource/droplocation")

    println(fileStreamDF.isStreaming)

    println(fileStreamDF.printSchema())

    val busDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    spark.udf.register("addTimeStamp",() => busDataFormat.format(Calendar.getInstance().getTime()))

    val trimmedDF = fileStreamDF.select($"value",$"major_category",$"minor_category",$"year",$"month",$"borough").withColumnRenamed("value","convictions")

    val trimmedDF2=trimmedDF.withColumn("timestamp",callUDF("addTimeStamp"))

    val query = trimmedDF2.writeStream.trigger(Trigger.Once).format("parquet")
      .option("checkpointLocation", "StreamCheckPoint3")
      .start("StructuresStreamingOP2/data/")

    query.stop





  }

}
