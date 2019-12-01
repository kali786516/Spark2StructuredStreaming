package com.dataframe.part31.OldSparkExamplesJson.kali.datastructures

/**
  * Created by kjfg254 on 11/20/2016.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, SparkConf}

case class Node (var value:Int,var Next:Node=null)

object NodeChain {

  def main(args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("YOUR_APP_NAME_USER").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

    //+------+---------+
    //|  3   | null    +
    //+------+---------+

    val first=new Node(value = 3)

    //+------+---------+   +-------+-------+
    //|  3   | null    +   |  5    | null  +
    //+------+---------+   +-------+-------+

    val middle=new Node(value = 5)

    //+------+---------+    +-------+-------+
    //|  3   | *-------+--->|  5    | null  +
    //+------+---------+    +-------+-------+

    first.Next=middle;

    //+------+---------+    +-------+-------+  + -------+-------+
    //|  3   | *-------+--->|  5    | null  +  |   7    | null  +
    //+------+---------+    +-------+-------+  +--------+-------+

    val last=new Node(value = 7)

    //+------+---------+    +-------+-------+    + -------+-------+
    //|  3   | *-------+--->|  5    | *-----+--->|   7    | null  +
    //+------+---------+    +-------+-------+    +--------+-------+

    middle.Next=last

    println(first)

    /*def PrintList(val node:Node): Unit =
    {
      while (node != null)
        {
           println(node.value)
           node=node.Next;
        }
    }*/
  }

}
