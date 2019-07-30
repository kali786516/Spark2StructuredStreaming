package com.dataframe.part12.kafka.consumer

/**
  * Created by kalit_000 on 6/13/19.
  */
/*
import kafka.serializer.StringDecoder
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.{Seconds,StreamingContext}
import org.apache.spark._
import  org.apache.spark.streaming._
 import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.kafka.KafkaUtils
import  org.apache.spark.streaming.kafka._
import com.datastax.spark.connector.streaming._
import org.apache.spark.streaming.kafka._
*/

object KafkaConsumerSqlServeData {

  def main(args: Array[String]): Unit = {

    /*

    Logger.getLogger("org").setLevel(Level.WARN) 
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[2]").setAppName("KafkaOffsetStreaming").set("spark.executor.memory", "1g") 
    val sc   = new SparkContext(conf) 
    val ssc  = new StreamingContext(sc, Seconds(2)) 

    val zkQuorm    = "localhost:2181" 
    val group      = "test-group2" 
    val topics     = Set("trade") 
    val numThreads = 1 
    val broker     = "localhost:9092"

    val kafkaParams = Map[String, String]("metadata.broker.list" -> broker)  
    val messages    = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)

    messages.print 

    ssc.start() 
    ssc.awaitTermination() 
    */

  }

}
