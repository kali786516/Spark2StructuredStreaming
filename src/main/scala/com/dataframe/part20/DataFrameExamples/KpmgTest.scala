package com.dataframe.part20.DataFrameExamples

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

/**
  * Created by kalit_000 on 5/23/19.
  */
object KpmgTest {
  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._


    val comments = spark.sparkContext.parallelize(List(("u1", "one two one"), ("u2", "three four three")))

    val wordCounts = comments.
      flatMap({case (user, comment) =>
        for (word <- comment.split(" ")) yield(((user, word), 1)) }).
      reduceByKey(_ + _)

    val output = wordCounts.
      map({case ((user, word), count) => (user, (word, count))}).
      groupByKey()

    output.foreach(println)

   /*

    1) Write a distributed program (using the Apache Spark API or similar) to find the average of an arbitrary sized set of integers using a fixed memory footprint
    Solution:-
      --###########################################################################################################
    package com.examples

    /**
      * Created by kalit_000 on 17/09/2015.
      */

    import java.util

    import org.apache.log4j.Logger
    import org.apache.log4j.Level
    import org.apache.spark.{SparkContext, SparkConf}
    import java.util.{Date,Random}
    import scala.collection.mutable.ArrayBuffer
    import math._


    object MemoryFootPrint
    {
      def main(args:Array[String]): Unit =
      {

        Logger.getLogger("org").setLevel(Level.WARN)
        Logger.getLogger("akka").setLevel(Level.WARN)

        val conf = new SparkConf().setMaster("local").setAppName("Memory Foot Print").set("spark.executor.memory", "1g")
        val sc = new SparkContext(conf)

        var logger = Logger.getLogger(this.getClass())

        val numbers=List(1, 2, 3, 4, 5)
        val distnumber=sc.parallelize(numbers)

        val countofnumlist=distnumber.count()

        val sumofnumlist=numbers.foldLeft(0)(_+_)

        val averageofnumber=sumofnumlist/countofnumlist

        print("Average of numbers in List:%s".format(averageofnumber))

        //logger.info()
        //val mb = 1024**8

        val mb=1024*16
        val runtime = Runtime.getRuntime
        logger.info("** Used Memory:  " + (runtime.totalMemory - runtime.freeMemory) / mb)
        logger.info("** Free Memory:  " + runtime.freeMemory / mb)
        logger.info("** Total Memory: " + runtime.totalMemory / mb)
        logger.info("** Max Memory:   " + runtime.maxMemory / mb)


      }
    }
    --###########################################################################################################
    2)Write a short paragraph discussing the pros and cons of Spark vs traditional Map Reduce

    Spark
    Pros:-
      In memory computation (disk only involved when data is too large this was introduced in spark 0.9 version) , computation is done is cache memory
      spark has different API (SPARK STREAMING,SPARK SQL,ML,Graph) so many use cases can be done in spark without learning new technologies
    spark uses Java,Python and Scala so developers can choose their own expertises ,Scala is functional and precise  so less lines of code for development,debugging and testing
    Fault Tolerance in spark streaming can be achieved by using check pointing logic (makes spark slower as data need to be replicated in two places check point folder and actual output folder)
    if kafka is used as messaging system spark streaming receiver can use kafka offset for fault tolerance so no check point folder required
      can run hive using spark by setting hive.execution.engine=spark which makes hive not to use traditional map reduce
      can process structured and unstructured data

    Cons:-
      earlier version of spark ran out of memory (<0.9 version)
    shuffle phase performance problem in Initial design (Eg if Map=2000 and Reduce = 6000 ie 2000*6000=12 million) Around 12 Million Shuffle files !!Performance Problems which is later resign
    shuffle file consolidation fixes the above error if R=Mapper C=Core (eg:if R=2000 and C=4 2000*4=8000 files huge improvement by set: spark.shuffle.consolidateFiles to true )
    spark is still getting mature
    not to good for large data sets processing better to process small sets of data if the file is too large break it down to several partitions
    spark takes up lot of memory resources which might be an issue for imapala (which runs on daemons)

    Map reduce:-

    Pros:-
      can process large sets of data (slow but completes without error)
    Hive is used as sql interface over map reduce java
      ML using mahout can process large data sets
      can process structured and unstructured data

    Cons:-
      writes data to disk which makes it slow as disk read is always slow compared to cache read
      legacy and traditional
    slow
    Mahout is slow


    3)Write a program to accurately calculate the distance between the following two sites Gatwick Airport (51.153662, -0.182063) and Sydney Opera House (-33.857197, 151.21514)

    Solution:-

    --###########################################################################################################
    package com.examples

    /**
      * Created by kalit_000 on 17/09/2015.
      */
    import org.apache.log4j.Logger
    import org.apache.log4j.Level
    import org.apache.spark.{SparkContext, SparkConf}
    import math._

    object DistanceCalculator
    {

      Logger.getLogger("org").setLevel(Level.WARN)
      Logger.getLogger("akka").setLevel(Level.WARN)

      val conf = new SparkConf().setMaster("local").setAppName("Distance Calculator")
      val sc = new SparkContext(conf)

      //radius of earth
      val R=6372.8

      def distiancefunc(lat1:Double,lon1:Double,lat2:Double,lon2:Double)=
      {
        val DistanceLat = (lat2 - lat1).toRadians
        val DistanceLon = (lon2 - lon1).toRadians

        val a = pow(sin(DistanceLat / 2), 2) + pow(sin(DistanceLon / 2), 2) * cos(lat1.toRadians) * cos(lat2.toRadians)

        val c = 2 * asin(sqrt(a))

        R * c
      }

      def main(args: Array[String]): Unit=
      {


        var logger = Logger.getLogger(this.getClass())
        if (args.length < 4) {
          logger.error("=> Less number of arguments passed please check")
          System.err.println("Usage: Lat1: 51.153662 Lon1: -0.182063 Lat2:-33.857197 Lon2:151.21514" )
          System.exit(1)
        }

        val Latitude1  =args(0).toDouble
        val Longitude1 =args(1).toDouble
        val Latitude2  =args(2).toDouble
        val Longitude2 =args(3).toDouble

        val output=distiancefunc(Latitude1,Longitude1,Latitude2,Longitude2)

        println("Distance between two desired points in kilometers:%s".format(output))
        println("Distance between two desired points in Miles:%s".format(output*0.62137))


      }
    }
    --OP:-
    Distance between two desired points in kilometers:17020.510314815558
    Distance between two desired points in Miles:10576.034494316942

    --###########################################################################################################

    4)Given the following example dataset:
      Sally A,B,A,B,B,C,A,B,D,A
    Fred B,A,A,A,B,C,D,A,C,A
    Sally B,B,B,A,A,A,B,C,A
    John A,B,A,B,F,H,B,A,B,B
    Write a distributed program (using the Apache Spark API or similar) to output the data in the following format:
      Sally – A [Count] B [Count]
      Fred – A [Count] B [Count]
      John – A [Count] B [Count]
      where Count is the relevant count for that argument.
    Assume that any future datasets, although in the same format, may contain arbitrary number of records with an arbitrary length of data points

    Solution:-
      (Hive)
    --###########################################################################################################
    CREATE EXTERNAL TABLE  KP_TEST
      (
        user_name                     string                   ,
        COMMENTS                      STRING

      )  ROW FORMAT DELIMITED FIELDS TERMINATED BY '\001'  STORED AS TEXTFILE LOCATION '/data/kp/kp_test';  ----  HDFS FOLDER (create hdfs folder and create a text file with data mentioned in the email)

    use default;select user_name,COLLECT_SET(text) from (select user_name,concat(sub,' ',count(comments)) as text  from kp_test LATERAL VIEW explode(split(comments,','))
      subView AS sub group by user_name,sub)w group by user_name;


    OP:-
      Sally    ["C 1","A 4","B 4","D 1"]
    Fred     ["C 2","A 5","B 2","D 1"]
    Sally    ["C 1","A 4","B 4"]
    John     ["F 1","A 3","B 5","H 1"]
    --###########################################################################################################

    Spark (Code):-

    --###########################################################################################################
    package com.examples

    /**
      * Created by kalit_000 on 17/09/2015.
      */
    import org.apache.log4j.Logger
    import org.apache.log4j.Level
    import org.apache.spark.sql.SQLContext
    import org.apache.spark.sql.hive.HiveContext
    import org.apache.spark.{SparkContext, SparkConf}
    import org.apache.spark.SparkContext._


    object HiveWordCount {

      def main(args: Array[String]): Unit =
      {
        Logger.getLogger("org").setLevel(Level.WARN)
        Logger.getLogger("akka").setLevel(Level.WARN)

        val conf = new SparkConf().setMaster("local").setAppName("HiveWordCount").set("spark.executor.memory", "1g")
        val sc = new SparkContext(conf)
        val sqlContext= new SQLContext(sc)

        val hc=new HiveContext(sc)

        hc.sql("CREATE EXTERNAL TABLE IF NOT EXISTS default.KP_TEST  (user_name string ,COMMENTS STRING )ROW FORMAT DELIMITED FIELDS TERMINATED BY '001'  STORED AS TEXTFILE LOCATION '/data/kp/kp_test' ")

        val op=hc.sql("select user_name,COLLECT_SET(text) from (select user_name,concat(sub,' ',count(comments)) as text  from default.kp_test LATERAL VIEW explode(split(comments,',')) subView AS sub group by user_name,sub)w group by user_name")

        op.collect.foreach(println)


      }

    }
    --###########################################################################################################


  */


  }

}
