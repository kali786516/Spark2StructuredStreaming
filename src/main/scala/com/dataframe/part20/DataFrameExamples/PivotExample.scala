package com.dataframe.part20.DataFrameExamples

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

/**
  * Created by kalit_000 on 5/14/19.
  */
object PivotExample {

  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._

    val df=spark.createDataFrame(List(("11",1753,"4"),("11",1682,"1"),("11",216,"4"),("11",2997,"4"),("11",1259,"3"))).toDF("user","movie","rating")

    df.show(false)

    /*
      +----+-----+------+
      |user|movie|rating|
      +----+-----+------+
      |11  |1753 |4     |
      |11  |1682 |1     |
      |11  |216  |4     |
      |11  |2997 |4     |
      |11  |1259 |3     |
      +----+-----+------+
    */

    df.groupBy("user").pivot("movie").agg(expr("coalesce(first(rating),3)").cast("double")).show(false)

    /*
      +----+---+----+----+----+----+
      |user|216|1259|1682|1753|2997|
      +----+---+----+----+----+----+
      |11  |4.0|3.0 |1.0 |4.0 |4.0 |
      +----+---+----+----+----+----+
    */

    //df.createOrReplaceTempView("test")

    //spark.sql("select * from (select user,movie,rating from test group by user) PIVOT(CAST(rating as DECIMAL(4,1)) FOR user in ('11')) )").show(false)

  }

}
