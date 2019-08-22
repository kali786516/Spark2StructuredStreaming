package com.dataframe.part20.DataFrameExamples

/**
  * Created by kalit_000 on 5/14/19.
  */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window


object SparkUDFExample {
  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._

    val sampleDF = spark.createDataFrame(List((1,"This is some sample data"),List(2,"go fuckyourself"))).toDF("id","text")

    spark.udf.register("capitalizeFirstUsingSpace",(fullString:String) => fullString.split(" ").map(_.capitalize).mkString(" "))

    spark.udf.register("test",(fullString:String,splitter:String) => fullString.split(splitter).map(_.capitalize).mkString(splitter))

    sampleDF.select($"id",callUDF("capitalizeFirstUsingSpace",$"text")).show(false)



  }

}
