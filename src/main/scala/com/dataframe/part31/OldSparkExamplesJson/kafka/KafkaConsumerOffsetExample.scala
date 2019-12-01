package com.dataframe.part31.OldSparkExamplesJson.kafka

/**
  * Created by kjfg254 on 11/21/2016.
  */
import java.util.Properties
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{TaskContext, SparkContext, SparkConf}
import org.apache.spark.streaming.kafka.{HasOffsetRanges, KafkaUtils, OffsetRange}
import kafka.serializer.StringDecoder
import sys.process._

object KafkaConsumerOffsetExample {
  def main(args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("Scala_Kafka_Consumer_DF").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

    val zkQuorm="localhost:2181"
    val group="test-group2"
    val topic=Set("first")
    val numThreads=1
    val broker="localhost:9092"
    val kafkaParams = Map[String, String]("metadata.broker.list" -> broker)
    val kafkaProps = new Properties()

    //Get max offset value from kafka logs folder using shell commands this is just for test , in rela time values should be saved in audit database and used as incremental jobs
    val maxoffset="C:\\Users\\kalit_000\\Desktop\\kafka\\kafka_2.11-0.8.2.1\\bin\\windows\\kafka-run-class.bat kafka.tools.GetOffsetShell  --broker-list localhost:9092 --partitions 0 --topic first --time -1".!!
    val maxoffsetValue=Seq(maxoffset).map(x =>x.split("\\:")).map(x => x(2)).toString().replace("List(","").replace(")","").replace(" ","")
    println("max offset value:-%s".format(maxoffsetValue.trim.toInt))

    //Get min offset value from kafka logs folders using shell commands this is just for test , in rela time values should be saved in audit database and used as incremental jobs
    val minoffset="C:\\Users\\kalit_000\\Desktop\\kafka\\kafka_2.11-0.8.2.1\\bin\\windows\\kafka-run-class.bat kafka.tools.GetOffsetShell  --broker-list localhost:9092 --partitions 0 --topic first --time -2".!!
    val minoffsetValue=Seq(minoffset).map(x =>x.split("\\:")).map(x => x(2)).toString().replace("List(","").replace(")","").replace(" ","")
    println("mix offset value:-%s".format(minoffsetValue.trim.toInt))

    // change these values to offsets that actually exist for the topic, you can extract these values from audit database if stored
    val offsetRanges = Array(
      OffsetRange(topic.toSet.head,0,minoffsetValue.trim.toInt,maxoffsetValue.trim.toInt)
    )


    val lines= KafkaUtils.createRDD[String, String, StringDecoder, StringDecoder](sc, kafkaParams, offsetRanges)

    lines.collect.foreach(println)

    println("Sum of rows consumes today:-%d".format(lines.count()))
    sc.stop()

  }
}
