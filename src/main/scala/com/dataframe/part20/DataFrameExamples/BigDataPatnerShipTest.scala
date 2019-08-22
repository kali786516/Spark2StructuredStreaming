package com.dataframe.part20.DataFrameExamples

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

/**
  * Created by kalit_000 on 5/23/19.
  */
object BigDataPatnerShipTest {
  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._

    val df = Seq(("Sri","123"),("Hari","786")).toDF("Name","ID")

    df.show(10)

  }
}

/*
 package com.examples

 //import classes for sql
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
                 distinct_tweets_string_op_two.registerTempTable("tweetphrasetable")
                 hiveCtx.sql("select * from tweetphrasetable limit 100").collect().foreach(println)

                 //Lets load pharese text file and register to temp table
                 println(s"Lets Load Phrases text file........")
                 //case class Phraseclass(text: String)
                 //println(s"Lets load phrases........")
                 val phrasepath=phrasefilepath
                 val phrase=sc.textFile(phrasepath).map(_.split(" ")).map(p => Phraseclass(p(0)))
                 phrase.registerTempTable("phrasetable")

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


 package com.examples

 //import classes for sql
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

   package com.examples


   //import classes for sql
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

   package com.examples

   //import classes for sql
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


   object WordCountTweetsCsv {
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
                   val distinct_tweets_string=distinct_tweets.map(x=>x.toString)

                   val csvop=distinct_tweets_string.flatMap(line =>line.split(" ")).map(word => (word,1)).reduceByKey(_+_).sortBy {case (key,value) => -value}.map { case (key,value) => Array(key,value).mkString(",") }
                   csvop.collect().foreach(println)

                   def merge(srcPath: String, dstPath: String): Unit =  {
                   val hadoopConfig = new Configuration()
                   val hdfs = FileSystem.get(hadoopConfig)
                   FileUtil.copyMerge(hdfs, new Path(srcPath), hdfs, new Path(dstPath), false, hadoopConfig, null)
                   }

                   val file = stagepath
                   FileUtil.fullyDelete(new File(file))

                   val destinationFile= outputpath
                   FileUtil.fullyDelete(new File(destinationFile))

                   csvop.saveAsTextFile(file)

                   merge(file, destinationFile)
     }
   }

*/
