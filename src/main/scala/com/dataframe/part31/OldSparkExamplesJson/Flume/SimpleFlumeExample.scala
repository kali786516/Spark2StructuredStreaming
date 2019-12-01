package com.dataframe.part31.OldSparkExamplesJson.Flume

/**
  * Created by kjfg254 on 11/21/2016.
  */
import com.datastax.spark.connector.streaming._
import kafka.serializer.StringDecoder
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.log4j.Logger
import org.apache.log4j.Level
//import scala.tools.nsc.transform.patmat.Logic.PropositionalLogic.True
//import org.apache.spark.streaming.{Seconds,StreamingContext}
import org.apache.spark._
import  org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.kafka.KafkaUtils
import com.datastax.spark.connector._
import org.apache.spark.streaming.kafka._
//import org.apache.spark.streaming.flume._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.util.IntParam

object SimpleFlumeExample {
  def main(args: Array[String]): Unit =
  {
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[2]").setAppName("testkali")
    val sc=new SparkContext(conf)
    val ssc= new StreamingContext(sc,Seconds(2))

    //count of events recieved from flume
    val host="locahost"
    val port="9093"
    //val stream=FlumeUtils.createStream(ssc,host,port,StorageLevel.MEMORY_ONLY_SER_2,enableDecompression = "TRUE")

    ssc.start()
    ssc.awaitTermination()
  }
}
