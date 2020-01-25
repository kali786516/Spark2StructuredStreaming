package com.dataframe.part36.arrayAndJsonDataExplode

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

object SparkJsonParsing {
  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("FlattenTest")
    //Logger.getLogger("org").setLevel(Level.WARN)
    //Logger.getLogger("akka").setLevel(Level.WARN)

    val spark = SparkSession.builder()
      .appName("FlattenTest")
      //.config("spark.sql.warehouse.dir", "C:\\Temp\\hive")
      .master("local[2]")
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    val stringTest =
      """{
                               "total_count": 123,
                               "page_size": 20,
                               "another_id": "gdbfdbfdbd",
                               "sen": [{
                                "id": 123,
                                "ses_id": 12424343,
                                "columns": {
                                    "blah": "blah",
                                    "count": 1234
                                },
                                "class": {},
                                "class_timestamps": {},
                                "sentence": "spark is good"
                               }]
                            }
                             """
    val result = List(stringTest)
    val githubRdd=spark.sparkContext.makeRDD(result)
    val gitHubDF=spark.read.json(githubRdd)
    gitHubDF.show()
    gitHubDF.printSchema()


    gitHubDF.registerTempTable("JsonTable")

    /*nested structure*/

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
