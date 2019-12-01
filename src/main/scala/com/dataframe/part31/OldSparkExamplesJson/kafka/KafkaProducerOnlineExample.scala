package com.dataframe.part31.OldSparkExamplesJson.kafka

/**
  * Created by kjfg254 on 11/21/2016.
  */

import kafka.producer.KeyedMessage
import kafka.producer.Producer
import kafka.producer.ProducerConfig
import java.util.Properties
import _root_.kafka.serializer.StringDecoder
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.streaming._
import org.apache.spark.streaming.{Seconds,StreamingContext}
import org.apache.spark._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.kafka.KafkaUtils


class SparkFuEvent(
                    val year: Int,
                    val month: Int,
                    val day: Int,
                    val hour: Int,
                    val minute: Int,
                    val second: Int,
                    val millis: Int,
                    val msg: String // What happened yo!?
                  )
object KafkaProducerOnlineExample {
  def main(args: Array[String]): Unit =
  {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[2]").setAppName("testkali")
    val sc=new SparkContext(conf)
    //val ssc= new StreamingContext(sc,Seconds(2))

    val props:Properties = new Properties()
    props.put("metadata.broker.list", "localhost:9092")
    props.put("serializer.class", "kafka.serializer.StringEncoder")

    val config= new ProducerConfig(props)
    val producer= new Producer[String,String](config)

    val evt0 = new SparkFuEvent(year=2015, month=1, day=20, hour=9, minute=32, second=27, millis=666, msg="What the foo?")
    val evt1 = new SparkFuEvent(year=2015, month=1, day=20, hour=9, minute=32, second=27, millis=667, msg="What the bar?")
    val evt2 = new SparkFuEvent(year=2015, month=1, day=20, hour=9, minute=32, second=27, millis=668, msg="What the baz?")
    val evt3 = new SparkFuEvent(year=2015, month=1, day=20, hour=9, minute=32, second=27, millis=669, msg="No! No! NO! What the FU!")

    val evts:List[SparkFuEvent] = evt0 :: evt1 :: evt2 :: evt3 :: Nil

    for (evt:SparkFuEvent <- evts) {
      producer.send(new KeyedMessage[String, String]("trade", evt.msg))
    }


  }
}
