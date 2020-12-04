package com.dataframe.part20.DataFrameExamples

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.mllib.rdd.RDDFunctions._
import scala.collection.mutable
import scala.collection.mutable.Queue
import scala.math.Ordering.Implicits._
import scala.collection.mutable.PriorityQueue

/**
  * Created by kalit_000 on 5/23/19.
  */
object VikrantSlidingScalaExample {
  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local")
      .config("spark.driver.memory","2g")
      .config("spark.sql.autoBroadcastJoinThreshold","29929")
      .config("spark.sql.shuffle.partitions","20")
      .config("spark.sql.inMemoryColumnarStorage.batchSize","234")
      .enableHiveSupport().getOrCreate()


    /*
    val test = spark.sparkContext.parallelize(1 to 100, 10)
      .sliding(3)
      .map(curSlice => (curSlice.sum / curSlice.size))
      .toLocalIterator

    println(test)*/


    /*

    https://stackoverflow.com/questions/27525272/apache-spark-dealing-with-sliding-windows-on-temporal-rdds

    https://stackoverflow.com/questions/23402303/apache-spark-moving-average/28863132


    spark.sparkContext.parallelize(1 to 100, 10)
      .sliding(3)
      .map(curSlice => (curSlice.sum / curSlice.size))
      .collect()

    val file=spark.textFile("C:\\Users\\srtummala\\Desktop\\Sri Useful\\paypal\\devops\\vikrant\\data.txt")

    val noheaders=file.zipWithIndex().filter(x => x._2 > 0)


    val op=noheaders.map(x => x._1.split("\\~"))
      .map(x => (x(0),x(1),x(2)))
      .map{case (accountnumber,datevalue,amount) =>
      ((accountnumber.toString+"-"+datevalue.substring(0,8)),(datevalue.substring(7,8).toDouble,amount.toDouble))}
      .reduceByKey((x,y) => ((((math.max(x._1,y._1)- math.min(x._1,y._1))),(x._2+y._2))))

     */

    /*
   * account_number~datevalue~amount
    -987~20150728~100
    -987~20150729~-50
    -987~20150730~100
    -987~20150804~0
    -987~20150807~0
    -987~20150916~-100


    (-987-201508,(3.0,0.0))
    (-987-201507,(1.0,150.0))
    (-987-201509,(6.0,-100.0))
   * */

    /*
    var q=new mutable.Queue[String]()

    val rddQueue = new Queue[(Int,String,String)]()

   val test=sc.parallelize(List("1~kali~sri"))

    val test2=test.map(x => x.split("\\~")).map(x => (x(0).toInt,x(1),x(2)))

    //test2.foreach(println)


    for (i <- test2) {

        rddQueue += i
        println(rddQueue.dequeue())
    }

   // insert into queue
    q+="kali"

    q+="sri"

    q.enqueue("super")

    println(q)

    //clear the queue

    q.clear()


    //priority Queue

    def diff(t2:(Int,Int))=math.abs(t2._1-t2._2)

    val x=new mutable.PriorityQueue[(Int,Int)]()(Ordering.by(diff))

    x.enqueue(1 -> 1)

    x.enqueue(1 -> 2)

    x.enqueue(1 -> 3)

    x.enqueue(1 -> 4)

    x.enqueue(1 -> 0)

    println(x)

    */

  }

}
