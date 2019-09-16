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

object WordcountTweets {
  def main(arg: Array[String]) {

    var logger = Logger.getLogger(this.getClass())

    println(s"Atleast two argument need to be passed")

    if (arg.length < 3) {
      logger.error("=> wrong parameters number")
      System.err.println("Usage: WordCountTweetsCsv inputpath:- /home/cloudera/tweets.json outputpath:- /home/cloudera/tweetswordcount.csv stagepath:-/home/cloudera/stageswordcount.csv" )
      System.exit(1)
    }

    val jobName = "WordCountTweetsCsv"
    val conf = new SparkConf().setAppName(jobName)
    val sc = new SparkContext(conf)
    val inputpath  = arg(0)
    val outputpath = arg(1)
    val stagepath  = arg(2)

    println(s"argument passed by user for input data $inputpath")

    println(s"argument passed by user for output data $outputpath")

    println(s"argument passed by user for output data $stagepath")

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
    val distinct_tweets=hiveCtx.sql(" select distinct(text) from tweets_table where text <> ''")
    val distinct_tweets_op=distinct_tweets.collect()
    val distinct_tweets_list=sc.parallelize(List(distinct_tweets_op))
    //val distinct_tweets_string=distinct_tweets.map(x=>x.toString)

    //val csvop=distinct_tweets_string.flatMap(line =>line.split(" ")).map(word => (word,1)).reduceByKey(_+_).sortBy {case (key,value) => -value}.map { case (key,value) => Array(key,value).mkString(",") }
    //csvop.collect().foreach(println)

    def merge(srcPath: String, dstPath: String): Unit =  {
      val hadoopConfig = new Configuration()
      val hdfs = FileSystem.get(hadoopConfig)
      FileUtil.copyMerge(hdfs, new Path(srcPath), hdfs, new Path(dstPath), false, hadoopConfig, null)
    }

    val file = stagepath
    FileUtil.fullyDelete(new File(file))

    val destinationFile= outputpath
    FileUtil.fullyDelete(new File(destinationFile))

    //csvop.saveAsTextFile(file)

    merge(file, destinationFile)
  }
}
