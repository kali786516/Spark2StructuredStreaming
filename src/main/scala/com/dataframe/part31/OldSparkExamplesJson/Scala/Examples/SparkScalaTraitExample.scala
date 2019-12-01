package com.dataframe.part31.OldSparkExamplesJson.Scala.Examples

/**
  * Created by kjfg254 on 11/21/2016.
  */
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.log4j.Logger
import org.apache.log4j.Level

trait SparkScalaTraitExample {
  Logger.getLogger("org").setLevel(Level.WARN)
  Logger.getLogger("akka").setLevel(Level.WARN)

  val conf = new SparkConf().setMaster("local").setAppName("My App2")
  val sc=new SparkContext(conf)
}
