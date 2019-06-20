package com.dataframe.RealTimeFraudDetection.CreditCardProducer

import java.io.File
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.{Date, Properties, Random, TimeZone}

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.csv.{CSVFormat, CSVParser}
import org.apache.kafka.clients.producer._
import com.google.gson.{Gson, JsonObject}
/**
  * Created by kalit_000 on 6/2/19.
  */


object TrasactionProducer {

  var applicationConf:Config                 = _
  val props                                  = new Properties()
  var topic:String                           =  _
  var producer:KafkaProducer[String, String] = _

  def load = {

    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConf.getString("kafka.bootstrap.servers"))
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, applicationConf.getString("kafka.key.serializer"))
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, applicationConf.getString("kafka.value.serializer"))
    props.put(ProducerConfig.ACKS_CONFIG, applicationConf.getString("kafka.acks"))
    props.put(ProducerConfig.RETRIES_CONFIG, applicationConf.getString("kafka.retries"))
    topic = applicationConf.getString("kafka.topic")

  }

  def getCsvIterator(fileName:String) = {
    val file      = new File(fileName)
    val csvParser = CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.DEFAULT)
    csvParser.iterator()
  }

  def publishJsonMsg(fileName:String) = {
    val gson:Gson         = new Gson
    val csvIterator       = getCsvIterator(fileName)
    val rand: Random      = new Random
    var count             = 0

    while (csvIterator.hasNext) {
      val record          = csvIterator.next()

      val obj: JsonObject = new JsonObject
      val isoFormat       = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      isoFormat.setTimeZone(TimeZone.getTimeZone("IST"));
      val d               = new Date()
      val timestamp       = isoFormat.format(d)
      val unix_time       = d.getTime


      //println("Transaction Details:" + record.get(0),record.get(1),record.get(2),record.get(3),timestamp, record.get(7),record.get(8),record.get(9), record.get(10), record.get(11))

      /*
      Example Data

       Transaction Record:
       {"cc_num":"cc_num","first":"first","last":"last","trans_num":"trans_num","trans_time":"2019-06-13 01:45:24","category":"category","merchant":"merchant","amt":"amt","merch_lat":"merch_lat","merch_long":"merch_long"}
       Sent data to partition: 1 and offset: 395
       Transaction Record: {"cc_num":"180094108369013","first":"John","last":"Holland","trans_num":"80f5177be11f0bcd768e06a0b1b294c8","trans_time":"2019-06-13 01:45:26","category":"personal_care","merchant":"Hills-Boyer","amt":"64","merch_lat":"39.011566","merch_long":"-119.937831"}
       Sent data to partition: 0 and offset: 395

      */

      obj.addProperty(TransactionKafkaEnum.cc_num, record.get(0))
      obj.addProperty(TransactionKafkaEnum.first, record.get(1))
      obj.addProperty(TransactionKafkaEnum.last, record.get(2))
      obj.addProperty(TransactionKafkaEnum.trans_num, record.get(3))
      obj.addProperty(TransactionKafkaEnum.trans_time, timestamp)
      //obj.addProperty(TransactionKafkaEnum.unix_time, unix_time)
      obj.addProperty(TransactionKafkaEnum.category, record.get(7))
      obj.addProperty(TransactionKafkaEnum.merchant, record.get(8))
      obj.addProperty(TransactionKafkaEnum.amt, record.get(9))
      obj.addProperty(TransactionKafkaEnum.merch_lat, record.get(10))
      obj.addProperty(TransactionKafkaEnum.merch_long, record.get(11))

      val json: String = gson.toJson(obj)
      println("Transaction Record: " + json)

      val producerRecord = new ProducerRecord[String, String](topic, json)
      //Round Robin Partitioner
      //val producerRecord = new ProducerRecord[String, String](topic, json.hashCode.toString, json)  //Hash Partitioner
      //val producerRecord = new ProducerRecord[String, String](topic, 1, json.hashCode.toString, json)  //Specific Partition
      //producer.send(producerRecord) //Fire and Forget
      //producer.send(producerRecord).get() /*Synchronous Producer */
      producer.send(producerRecord, new MyProducerCallback) /*Asynchrounous Produer */
      Thread.sleep(rand.nextInt(3000 - 1000) + 1000)
    }

  }

  class MyProducerCallback extends Callback {
    def onCompletion(recordMetadata: RecordMetadata, e: Exception) {
      if (e != null) System.out.println("AsynchronousProducer failed with an exception" + e)
      else {
        System.out.println("Sent data to partition: " + recordMetadata.partition + " and offset: " + recordMetadata.offset)
      }
    }

  }

  def main(args: Array[String]) {

    applicationConf = ConfigFactory.parseFile(new File(args(0)))
    println(applicationConf.getString("kafka.producer.file"))
    load
    producer = new KafkaProducer[String, String](props)
    val file = applicationConf.getString("kafka.producer.file")
    publishJsonMsg("/Users/kalit_000/Downloads/gitCode/Spark2StructuredStreaming/src/main/resources/data/transactions.csv")

  }



}
