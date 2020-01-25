package com.dataframe.part37.sparkreadrestapi

import org.apache.spark.sql._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs._
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StructType
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.sql.functions.{explode, expr, posexplode, when}


object SparkReadRestApi {

  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("RestApi")
    //Logger.getLogger("org").setLevel(Level.WARN)
    //Logger.getLogger("akka").setLevel(Level.WARN)

    val spark = SparkSession.builder()
      .appName("RestApi")
      //.config("spark.sql.warehouse.dir", "C:\\Temp\\hive")
      .master("local[2]")
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    val url = "https://api.github.com/users/defunkt"
    val result = List(scala.io.Source.fromURL(url).mkString)
    val githubRdd2 = spark.sparkContext.makeRDD(result)
    val githubDf = spark.read.json(githubRdd2)
    githubDf.printSchema()

    githubDf.registerTempTable("JsonTable")

    /* Flatten nested data*/

    spark.sql("with cte as" +
      "(" +
      "select explode(sen) as senArray  from JsonTable" +
      "), cte_2 as" +
      "(" +
      "select senArray.ses_id,senArray.ses_id,senArray.columns.* from cte" +
      ")" +
      "select * from cte_2"
    ).show()

    spark.stop()

  }

}
