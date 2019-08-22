package com.dataframe.part31.OldSparkExamples.kafka

/**
  * Created by kjfg254 on 11/21/2016.
  */
import org.apache.spark.SparkConf
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark._
import java.sql.{ResultSet, DriverManager, Connection}
import kafka.producer.KeyedMessage
import kafka.producer.Producer
import kafka.producer.ProducerConfig
import java.util.Properties
import org.apache.spark.streaming.{Seconds,StreamingContext}
import org.apache.spark._
import com.datastax.spark.connector.streaming._
import kafka.serializer.StringDecoder
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.log4j.Logger
import org.apache.log4j.Level
//import org.apache.spark.streaming.{Seconds,StreamingContext}
import org.apache.spark._
import  org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.kafka.KafkaUtils
import com.datastax.spark.connector._
import org.apache.spark.streaming.kafka._

object KafkaConsumerToCassandra {
  case class customer(customerid:Int,storeid: Int,territoryid: Int,accountnumber: String)

  def main(args:Array[String]): Unit =
  {
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[2]").set("spark.cassandra.connection.host", "127.0.0.1").setAppName("SqlServerKafkaReciever")
    val sc=new SparkContext(conf)
    val ssc= new StreamingContext(sc,Seconds(2))

    val zkQuorm="localhost:2181"
    val group="test-group2"
    val topics="trade"
    val numThreads=1
    val broker="localhost:9091"
    val kafkaParams = Map[String, String]("metadata.broker.list" -> broker)

    /*create topic map folder*/
    val topicMap=topics.split(",").map((_,numThreads.toInt)).toMap

    /**create kafka stream*/
    val stream=KafkaUtils.createStream(ssc,zkQuorm,group,topicMap)

    /*insert in to cassandra table */
    stream.map(_._2.split("~")).map(p => customer (p(0).toInt,p(1).toInt,p(2).toInt,p(3))).saveToCassandra("adw","customer",SomeColumns("customerid","storeid","territoryid","accountnumber"))

    val lines=stream.map(_._2)

    /*write ahead logs*/
    ssc.checkpoint("C:\\scalatutorials\\sqlsereverkakfareceiver")

    /*print the lines*/
    lines.print

    ssc.start()

    ssc.awaitTermination()
  }


}
