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

object CassandraToKafkaProducer {
  case class TestTable (TRADE_ID:String,TRADE_PRICE: String)

  def main(args: Array[String]): Unit =
  {
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[2]").setAppName("testkali2").set("spark.cassandra.connection.host", "127.0.0.1")
    val sc=new SparkContext("local","test",conf)
    //val ssc= new StreamingContext(sc,Seconds(2))

    print("Test kali Spark Cassandra")

    //val cc = new org.apache.spark.sql.cassandra.CassandraSQLContext(sc)

    //val p=cc.sql("select * from people.person")

    //p.collect().foreach(println)

    val props:Properties = new Properties()
    props.put("metadata.broker.list", "localhost:9092")
    props.put("serializer.class", "kafka.serializer.StringEncoder")

    val config= new ProducerConfig(props)
    val producer= new Producer[String,String](config)

    //val x=p.collect().mkString("\n").replace("[","").replace("]","").replace(",","~")

    //producer.send(new KeyedMessage[String, String]("trade", x))

  }
}
