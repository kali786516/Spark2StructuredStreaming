package com.dataframe.part31.OldSparkExamples.kali.datastructures

/**
  * Created by kjfg254 on 11/20/2016.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import scala.collection.mutable
import scala.collection.mutable.Queue
import scala.math.Ordering.Implicits._
import scala.collection.mutable.PriorityQueue
import org.apache.spark.mllib.rdd.RDDFunctions._

//Queu:- is FIFO

// similar to sainsburys q at checkouts

//Enqueue and Dequeue

//Enqueue is insert operation

//Peek returns head item of the q

//clear clears the q

//Dequeue is remove operation , get data operation

//Priority Queue

//police office complaints queue

object Queue {
  def main(args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("YOUR_APP_NAME_USER").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

    /*
    var q=new mutable.Queue[String]()
    val rddQueue = new Queue[(Int,String,String)]()
    val test=sc.parallelize(List("1~kali~sri"))
    val test2=test.map(x => x.split("\\~")).map(x => (x(0).toInt,x(1),x(2)))
   */
    //test2.foreach(println)

    /*
        for (i <- test2) {
            rddQueue += i
            println(rddQueue.dequeue())
        }
    */
    val file=sc.textFile("C:\\Users\\kalit_000\\Desktop\\vikranth\\data.txt")

    val noheaders=file.zipWithIndex().filter(x => x._2 > 0)

    val op=noheaders
      //.sliding(5).map(x => x.sum / x.size)
      .map(x => x._1.split("\\~")).sliding(2)
      .map(x => x(1).toList)
    // .map(x => x.sliding(5))
    // .map(x => x.sum)

    op.foreach(println)





    //.map{case (accountnumber,datevalue,amount) => ((accountnumber.toString,(datevalue,amount)))}

    //op.foreach(println)

    /*
        val test=sc.parallelize(1 to 100, 10).sliding(3)
                 //.map(curSlice => (curSlice.sum / curSlice.size))
                 .foreach(println)
    */


    // test.foreach(println)
    //.reduceByKey((x,y) => ((((math.max(x._1,y._1)- math.min(x._1,y._1))),(x._2+y._2))))


    /*
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
