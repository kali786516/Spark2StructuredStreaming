package com.dataframe.part22.strcuturedStreamingExamples

/**
  * Created by kalit_000 on 5/17/19.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

object ncStream {
  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark = SparkSession.builder
      .master("local[3]")
      .appName("Fraud Detector")
      //.config("spark.driver.memory","2g")
      //.config("spark.cassandra.connection.host","localhost")
      //.enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    println(args(0))
    println(args(1))

    val host=args.lift(0).getOrElse("localhost")
    val portValue=args.lift(1).getOrElse("9999")

    val socketDS = spark.readStream.format("socket").option("host",host).option("port",portValue).load

    val words=socketDS.as[String].flatMap(x => x.split(" "))

    val wordsCount = words.groupBy("value").count()

    val query = wordsCount.writeStream.format("console").outputMode("complete").start

    //val query = wordsCount.writeStream.format("memory").queryName("simpleCount").outputMode("complete").start

    //spark.sql("select * from simpleCount").show()

    query.awaitTermination()

  }

}
