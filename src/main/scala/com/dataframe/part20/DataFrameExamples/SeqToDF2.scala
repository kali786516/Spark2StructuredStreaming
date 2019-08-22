package com.dataframe.part20.DataFrameExamples

/**
  * Created by kalit_000 on 5/14/19.
  */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType

object SeqToDF2 {
  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._

    val df=spark.createDataFrame(List(("sri",123),("Hari",1234))).toDF("Username","Id")

    df.printSchema()

    df.selectExpr("Username","Id","Id as Id2").withColumn("Idcalc",$"Id" - 1).drop("Id").show(10,false)

    val finacesDF = spark.sql("Select * from parquet.`Output/finances-small`")

    finacesDF.createOrReplaceTempView("Finances")

    finacesDF.orderBy("Amount").limit(5).show()


  }

}
