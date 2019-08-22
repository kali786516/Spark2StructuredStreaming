package com.dataframe.part20.DataFrameExamples

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

/**
  * Created by kalit_000 on 5/14/19.
  */
object AggOutPutTest {

  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._

    val fianceDetails = spark.read.json("Output/finances-small-account-details")

    /*went to movie*/
    fianceDetails.select($"AccountNumber",$"UniqueTransactionDescrioptions",
      array_contains($"UniqueTransactionDescrioptions","Movies").as("WentToMovies")
    ).show(false)

    /*did not went to movie*/
    fianceDetails.select($"AccountNumber",$"UniqueTransactionDescrioptions",
      array_contains($"UniqueTransactionDescrioptions","Movies").as("WentToMovies")
    ).where(!$"WentToMovies").show(false)

    /*get size of array*/
    fianceDetails.select($"AccountNumber",size($"UniqueTransactionDescrioptions").as("CountOfUniqueTransactionTypes")).show(false)

    /*sort*/
    fianceDetails.select($"AccountNumber",size($"UniqueTransactionDescrioptions").as("CountOfUniqueTransactionTypes"),
      sort_array($"UniqueTransactionDescrioptions",asc = false).as("OrderedUniqueTransctionDescriptions")
    ).show(truncate = false)


  }

}
