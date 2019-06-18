package com.dataframe.strcuturedStreamingExamples

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object TitanicParquetConverter {

  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("BikeBuyersDecisionTrees")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val spark = SparkSession.builder().appName("Examine data about passensgers on the titanic").master("local[*]").getOrCreate()
    val sc = spark.sparkContext

    val rawData=spark.read.format("csv")
      .option("header","true")
      .load("sparkMLDataSets/titanic.csv")

    println("-" * 100)

    println("Sample Data 10 Records")
    rawData.show(10)

    println(rawData.count())

    println("-" * 100)

    println("cast data sets")

    rawData.createOrReplaceTempView("rawDataTable")

    val dataSet = spark.sql("select cast(Survived as double) as Survived ,cast(Pclass as float) as Pclass," +
      "Sex,cast(Age as double) as Age ,cast(Fare as double) as Fare," +
      "Embarked from rawDataTable")

    dataSet.show(10,false)

    dataSet.write.parquet("TitanicParquetData/op")

    spark.stop()

  }

}
