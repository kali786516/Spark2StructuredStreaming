package com.dataframe.part20.DataFrameExamples

/**
  * Created by kalit_000 on 5/14/19.
  */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType

object SeqToDF {
  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
                          .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._

    val df = Seq(("Sri","123"),("Hari","786")).toDF("Name","ID")

    val schemaUntyped = new StructType()
      .add("name1", "string")
      .add("id2", "int")

    val df2=spark.createDataFrame(df.rdd,schema = schemaUntyped)

    df2.printSchema()




  }

}
