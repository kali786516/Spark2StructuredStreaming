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
import org.apache.hadoop.fs.{Path, FileUtil, FileSystem}
import java.io.FileFilter
import java.io.File
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs._


object IdOfUsersThatTweeted {
  def main(arg: Array[String]) {

    var logger = Logger.getLogger(this.getClass())

    println(s"Atleast two argument need to be passed")

    if (arg.length < 2) {
      logger.error("=> wrong parameters number")
      System.err.println("Usage: IdOfUsersThatTweeted inputpath:- /home/cloudera/tweets.json outputpath:- /home/cloudera/bdp")
      System.exit(1)
    }

    val jobName = "IdOfUsersThatTweeted"
    val conf = new SparkConf().setAppName(jobName)
    val sc = new SparkContext(conf)
    val inputpath = arg(0)
    val outputpath = arg(1)

    println(s"argument passed by user for input data $inputpath")

    println(s"argument passed by user for output data $outputpath")

    logger.info("=> jobName \"" + jobName + "\"")
    logger.info("=> inputpath \"" + inputpath + "\"")
    logger.info("=> outputpath \"" + outputpath + "\"")

    val hiveCtx = new HiveContext(sc)
    import hiveCtx._


    //val path = "/home/cloudera/bdp/tweets.json"
    val tweets = hiveCtx.jsonFile(inputpath)
    tweets.printSchema()
    tweets.registerTempTable("tweets_table")


    //disitnct id who tweeted
    println(s"Problem 2:-Finding ids of all users that tweeted........")
    val distinctid = hiveCtx.sql(" select distinct(id) from tweets_table where text <> ''")
    val distinctid_op = distinctid.collect()
    val rdd = sc.parallelize(distinctid_op)

    val destinationFile= outputpath
    FileUtil.fullyDelete(new File(destinationFile))
    rdd.saveAsTextFile(destinationFile)


  }
}
