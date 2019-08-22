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

object TopNPhrases {

  case class tweetphraseclass(text: String)
  case class Phraseclass(text: String)

  def main(arg: Array[String]) {

    var logger = Logger.getLogger(this.getClass())

    println(s"Atleast four argument need to be passed")

    if (arg.length < 4) {
      logger.error("=> wrong parameters number")
      System.err.println("Usage: TopNPhrases tweet_json_file_path:- /home/cloudera/tweets.json phrase_file_path:- /home/cloudera/tweetswordcount.csv output_path:-/home/cloudera/stageswordcount.csv N_Values:- 10" )
      System.exit(1)
    }

    val jobName = "TopNPhrases"
    val conf = new SparkConf().setAppName(jobName)
    val sc = new SparkContext(conf)
    val tweetjasonfilepath  = arg(0)
    val phrasefilepath      = arg(1)
    val outputfilepath      = arg(2)
    val n_value             = arg(3)

    println(s"argument passed by user for tweet json file data $tweetjasonfilepath")

    println(s"argument passed by user for phrase file data $phrasefilepath")

    println(s"argument passed by user for output file data $outputfilepath")

    println(s"argument passed by user for n_value $n_value")

    logger.info("=> jobName \"" + jobName + "\"")
    logger.info("=> tweetjasonfilepath \"" + tweetjasonfilepath + "\"")
    logger.info("=> phrasefilepath \"" + phrasefilepath + "\"")
    logger.info("=> outputfilepath \"" + outputfilepath + "\"")
    logger.info("=> n_value \"" + n_value + "\"")

    val hiveCtx = new HiveContext(sc)
    import hiveCtx._


    //val path = "/home/cloudera/bdp/tweets.json"
    val tweets = hiveCtx.jsonFile(tweetjasonfilepath)
    tweets.printSchema()
    tweets.registerTempTable("tweets_table")


    //disitnct id who tweeted
    println(s"Lets create tweeted phrases rdd........")
    val distinct_tweets=hiveCtx.sql(" select distinct(text) from tweets_table where text <> ''")
    val distinct_tweets_op=distinct_tweets.collect()
    val distinct_tweets_list=sc.parallelize(List(distinct_tweets_op))
    val distinct_tweets_string=distinct_tweets.map(x=>x.toString)

    val distinct_tweets_string_op=distinct_tweets_string.flatMap(line =>line.split(" ")).map(word => word)

    //case class tweetphraseclass(text: String)

    val distinct_tweets_string_op_two=distinct_tweets_string_op.map(_.split(" ")).map(p => tweetphraseclass(p(0)))

    //distinct_tweets_string_op_two.registertemptable("tweetphrasetable")
    hiveCtx.sql("select * from tweetphrasetable limit 100").collect().foreach(println)

    //Lets load pharese text file and register to temp table
    println(s"Lets Load Phrases text file........")
    //case class Phraseclass(text: String)
    //println(s"Lets load phrases........")
    val phrasepath=phrasefilepath
    val phrase=sc.textFile(phrasepath).map(_.split(" ")).map(p => Phraseclass(p(0)))
   // phrase.registerTempTable("phrasetable")

    //Lets do the calculation for top N phrases
    println(s"Lets calculate top N phrases........")
    val sql1="select p.text,count(1) as count_of_times from phrasetable as p inner join tweetphrasetable as t on p.text=t.text group by p.text order by count_of_times desc limit"
    val sql2=n_value
    val sql3=sql1+" "+sql2
    val topnphrases=hiveCtx.sql(sql3)
    val topnphrases_collect=topnphrases.collect()
    val rdd = sc.parallelize(topnphrases_collect)
    rdd.collect().foreach(println)

    val destinationFile= outputfilepath
    FileUtil.fullyDelete(new File(destinationFile))

    rdd.saveAsTextFile(destinationFile)

  }
}
