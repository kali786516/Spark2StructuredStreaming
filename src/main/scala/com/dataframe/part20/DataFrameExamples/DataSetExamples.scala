package com.dataframe.part20.DataFrameExamples

/**
  * Created by kalit_000 on 5/15/19.
  */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.types.{BooleanType, StructField, StructType}

case class test(column:Boolean)

object DataSetExamples {

  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._

    val schema=StructType(List(StructField("test",BooleanType,true)))

    val rdd= spark.sparkContext.parallelize((List(Row(0),Row(true),Row("stuff"))))

    val data = Seq(test(true))

    //data frame test
    val df = spark.createDataFrame(rdd,schema)

    //val ds=spark.createDataset(rdd)

    df.collect()

    //ds.collect


  }
}
