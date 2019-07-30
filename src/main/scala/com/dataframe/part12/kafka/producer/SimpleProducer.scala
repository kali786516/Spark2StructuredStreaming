package com.dataframe.part12.kafka.producer

import java.io.File
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.{Date, Properties, Random, TimeZone}

import com.google.gson.{Gson, JsonObject}
import com.typesafe.config.Config
import org.apache.commons.csv.{CSVFormat, CSVParser}
import org.apache.kafka.clients.producer.{KafkaProducer, _}

object SimpleProducer {

  var applicationConf:Config                 = _
  val props                                  = new Properties()
  var topic:String                           =  _
  var producer:KafkaProducer[String, String] = _

  def load = {

    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.ACKS_CONFIG,"all")
    props.put(ProducerConfig.RETRIES_CONFIG, "0")
    topic = "creditcardTransaction"
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
      if (count < 1) {
        println(count)

      count               = count + 1
      val record          = csvIterator.next()

      val obj: JsonObject = new JsonObject
      val isoFormat       = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      isoFormat.setTimeZone(TimeZone.getTimeZone("IST"));
      val d               = new Date()
      val timestamp       = isoFormat.format(d)
      val unix_time       = d.getTime

      obj.addProperty(TransactionKafkaEnum.cc_num, record.get(0))
      obj.addProperty(TransactionKafkaEnum.first_column, record.get(1))
      obj.addProperty(TransactionKafkaEnum.last_column, record.get(2))
      obj.addProperty(TransactionKafkaEnum.trans_num, record.get(3))
      obj.addProperty(TransactionKafkaEnum.trans_time, "trans_time")
      //obj.addProperty(TransactionKafkaEnum.unix_time, unix_time)
      obj.addProperty(TransactionKafkaEnum.category_column, record.get(7))
      obj.addProperty(TransactionKafkaEnum.merchant_column, record.get(8))
      obj.addProperty(TransactionKafkaEnum.amt_column, record.get(9))
      obj.addProperty(TransactionKafkaEnum.merch_lat, record.get(10))
      obj.addProperty(TransactionKafkaEnum.merch_long, record.get(11))

      val json: String = gson.toJson(obj)
      println("Transaction Record: " + json)

      val producerRecord = new ProducerRecord[String, String](topic, json)
      producer.send(producerRecord, new MyProducerCallback)
      Thread.sleep(rand.nextInt(3000 - 1000) + 1000)

    }
    else {
        println(count)
        val record          = csvIterator.next()

        val obj: JsonObject = new JsonObject
        val isoFormat       = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("IST"));
        val d               = new Date()
        val timestamp       = isoFormat.format(d)
        val unix_time       = d.getTime

        obj.addProperty(TransactionKafkaEnum.cc_num, record.get(0))
        obj.addProperty(TransactionKafkaEnum.first_column, record.get(1))
        obj.addProperty(TransactionKafkaEnum.last_column, record.get(2))
        obj.addProperty(TransactionKafkaEnum.trans_num, record.get(3))
        obj.addProperty(TransactionKafkaEnum.trans_time, timestamp)
        //obj.addProperty(TransactionKafkaEnum.unix_time, unix_time)
        obj.addProperty(TransactionKafkaEnum.category_column, record.get(7))
        obj.addProperty(TransactionKafkaEnum.merchant_column, record.get(8))
        obj.addProperty(TransactionKafkaEnum.amt_column, record.get(9))
        obj.addProperty(TransactionKafkaEnum.merch_lat, record.get(10))
        obj.addProperty(TransactionKafkaEnum.merch_long, record.get(11))

        val json: String = gson.toJson(obj)
        println("Transaction Record: " + json)

        val producerRecord = new ProducerRecord[String, String](topic, json)
        producer.send(producerRecord, new MyProducerCallback)
        Thread.sleep(rand.nextInt(3000 - 1000) + 1000)

      }
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
    load
    producer = new KafkaProducer[String, String](props)
    publishJsonMsg("/Users/kalit_000/Downloads/FlinkStreamAndSql/src/main/resources/sourceExampleData/transactions.csv")

  }

}
