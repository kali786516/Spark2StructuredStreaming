package com.dataframe.part31.OldSparkExamplesJson.kali.datastructures

/**
  * Created by kjfg254 on 11/20/2016.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, SparkConf}

// Linked list has head pointer and tail pointer , list is a single chain of nodes
//operation on Linked List:- add,remove,enumerate and find

object LinkedList {
  def main(args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("YOUR_APP_NAME_USER").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

    //--------+
    // List(1)|
    //--------+
    val x=List(1)
    println(x)

    //add element to start of the list in scala

    //-----------+
    // List(0,1) |
    //-----------+
    val y=0::x
    println(y)

    // print head of list (1st element)
    println(y.head)

    //print tail of list (last element)
    println(y.tail)

    //remove element from list

    def remove(n:Int,list:List[Int])=list diff List(n)

    println(remove(1,y))

    //println(y)

    //enumerate list in scala

    for (i <- y) yield i




  }


}
