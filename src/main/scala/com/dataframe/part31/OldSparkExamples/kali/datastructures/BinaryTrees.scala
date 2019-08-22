package com.dataframe.part31.OldSparkExamples.kali.datastructures

/**
  * Created by kjfg254 on 11/20/2016.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, SparkConf}

//Citi bank example Director --> svp --> vp --> avp --> officer

// Parent has several nodes left child or parent child

// Adding Data :-

// empty tree becomes root node case 1 :- 4

//smaller value added to left case 2:- 2 , 1

//larger value added to right case 3:- 6 ,7

//   --4---
//   2   6
//  1   4  7

object BinaryTrees {

  def main(args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("YOUR_APP_NAME_USER").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)


  }


}
