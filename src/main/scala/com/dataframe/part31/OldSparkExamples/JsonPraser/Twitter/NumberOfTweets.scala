package com.dataframe.part31.OldSparkExamples.JsonPraser.Twitter

/**
  * Created by kjfg254 on 11/20/2016.
  */
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.log4j.Logger

object NumberOfTweets {
  def main(arg: Array[String]) {

    var logger = Logger.getLogger(this.getClass())

    println(s"Atleast one argument need to be passed")

    if (arg.length < 1) {
      logger.error("=> wrong parameters number")
      System.err.println("Usage: NumberOfTweets /home/cloudera/tweets.json ")
      System.exit(1)
    }

    val jobName = "NumberOfTweets"
    val conf = new SparkConf().setAppName(jobName)
    val sc = new SparkContext(conf)
    val path = arg(0)

    println(s"argument passed by user $path ")

    logger.info("=> jobName \"" + jobName + "\"")
    logger.info("=> path \"" + path + "\"")

    val hiveCtx = new HiveContext(sc)
    import hiveCtx._


    //val path = "/home/cloudera/bdp/tweets.json"
    val tweets = hiveCtx.jsonFile(path)
    tweets.printSchema()
    tweets.registerTempTable("tweets_table")


    val distinctcountoftweets = hiveCtx.sql("select count(1) from (select distinct(text) from tweets_table)a").collect()
    val sqltweetcount = distinctcountoftweets.head.getLong(0)
    println(s"total number of distinct tweets $sqltweetcount")

  }
}
